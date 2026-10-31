# Spring Boot Bugs That Only Show Up in Production
[![CI](https://github.com/sahiljasrotia/spring-boot-production-bugs/actions/workflows/ci.yml/badge.svg)](https://github.com/sahiljasrotia/spring-boot-production-bugs/actions/workflows/ci.yml)

Demo code for the video series on [The Java Loom](https://www.youtube.com/@thejavaloom), one video
per bug. Every bug here passes on one laptop with one request at a time, and breaks with more
instances or real traffic. Each one is reproduced, then fixed, side by side.

| # | Bug | Broken | Fixed | Video |
|---|---|---|---|---|
| 1 | `@Cacheable` serves stale data | in-memory cache per instance | Redis cache + `@CacheEvict` | coming soon |
| 2 | `@Scheduled` runs on every instance | plain `@Scheduled` | ShedLock | coming soon |
| 3 | `@Transactional` ignored on self-call | `this.saveOrderAndPayment()` | method moved to another bean | coming soon |
| 4 | `LazyInitializationException` | lazy collection read in `@Async` | `@EntityGraph` | coming soon |
| 5 | Lost updates under concurrency | read → subtract → save | atomic `UPDATE` / `@Version` | coming soon |

Code for bug N is in `src/main/java/com/thejavaloom/prodbugs/bugN...`.

Java 21 · Spring Boot 3.5 · H2 by default, PostgreSQL + Redis optional

## Run it - no Docker needed

```bash
mvn package -DskipTests       # builds target/prod-bugs.jar
java -jar target/prod-bugs.jar
```

By default the app uses H2, a database in a file (`./data`). Every instance you start shares it,
so the multi-server bugs work on one laptop. Only one step needs more: the **fixed** version of
bug 1 uses Redis. On Windows, install [Memurai Developer](https://www.memurai.com/get-memurai)
(free, Redis-compatible, runs as a Windows service on port 6379). On macOS/Linux, any local Redis.

Production-like setup (PostgreSQL + Redis): `docker compose up -d`, then add
`--spring.profiles.active=docker` to every `java -jar` command below.

Tests need nothing extra:

```bash
mvn test                              # the fixes - all green
mvn test -Dtest=SelfInvocationBugDemo # bug 3 - red on purpose
mvn test -Dtest=LazyLoadingBugDemo    # bug 4 - red on purpose
mvn test -Dtest=LostUpdateBugDemo     # bug 5 - red on purpose
```

`demo.http` has every request below, ready to click in VS Code (REST Client extension) or IntelliJ.

**In VS Code:** open this folder, install the recommended extensions, then pick a configuration in
Run and Debug: `Bugs 3-5: one server`, `Bug 1: two servers`, `Bug 2 broken: three servers` or
`Bug 2 fixed: three servers`. They start every instance for you.

---

## Bug 1 - `@Cacheable` stale data (two instances)

```bash
java -jar target/prod-bugs.jar --server.port=8080
java -jar target/prod-bugs.jar --server.port=8081    # second terminal
```

```bash
curl -X POST localhost:8080/bug1/reset                          # price = 999
curl localhost:8081/bug1/broken/price/1                         # 999 - now cached on 8081
curl -X PUT "localhost:8080/bug1/broken/price/1?value=499"      # update + evict on 8080
curl localhost:8080/bug1/broken/price/1                         # 499 - correct
curl localhost:8081/bug1/broken/price/1                         # 999 - STALE
```

Same steps with `fixed` instead of `broken` (needs Redis or Memurai running): both instances return 499. `@CacheEvict` was never the
problem - the cache lived in each JVM's own memory. Restart both instances before re-running, so their local caches start empty.

## Bug 2 - `@Scheduled` on every instance (three instances)

```bash
java -jar target/prod-bugs.jar --server.port=8080 --demo.bug2.mode=broken
java -jar target/prod-bugs.jar --server.port=8081 --demo.bug2.mode=broken
java -jar target/prod-bugs.jar --server.port=8082 --demo.bug2.mode=broken
```

The job fires every 30 seconds on the clock. After one run:

```bash
curl localhost:8080/bug2/emails     # emailsPerUser: 3 each - one per instance
curl -X DELETE localhost:8080/bug2/emails
```

Restart all three with `--demo.bug2.mode=fixed`: one email per user per run. Look at the
`shedlock` table to see which instance took the lock.

## Bug 3 - `@Transactional` self-invocation

```bash
curl -X POST localhost:8080/bug3/broken/orders    # 402, but ordersInDb: 1, paymentsInDb: 0
curl -X DELETE localhost:8080/bug3/orders
curl -X POST localhost:8080/bug3/fixed/orders     # 402, ordersInDb: 0 - rolled back
```

Why the test suite misses it: `SelfInvocationTest.directCallRollsBack_soTheBugLooksImpossible` calls
`saveOrderAndPayment()` directly, through Spring's proxy, and passes. Production calls it from
`placeOrderBroken()`, inside the class, where there is no proxy.

## Bug 4 - `LazyInitializationException`

```bash
curl -X POST localhost:8080/bug4/seed                 # returns orderId
curl localhost:8080/bug4/controller/{orderId}         # works - open-in-view keeps the session open
curl localhost:8080/bug4/async/broken/{orderId}       # LazyInitializationException
curl localhost:8080/bug4/async/fixed/{orderId}        # works - @EntityGraph
```

The controller path works only because of `spring.jpa.open-in-view=true`. Kafka listeners,
`@Scheduled` jobs and `@Async` methods never get that, so the same entity code fails there.
`FetchType.EAGER` would also "fix" it - by loading every line on every query, forever.

## Bug 5 - Lost updates

```bash
curl -X POST localhost:8080/bug5/reset                       # stock = 100
curl -X POST localhost:8080/bug5/broken/load                 # 100 concurrent purchases -> stock > 0
curl -X POST localhost:8080/bug5/reset
curl -X POST localhost:8080/bug5/atomic/load                 # stock = 0
curl -X POST localhost:8080/bug5/reset
curl -X POST localhost:8080/bug5/optimistic/load             # versionedStock = 0
```

## License

MIT
