package kr.pe.karsei.reactorprac;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;

@Slf4j
public class TestingTest {
    @Test
    void testWithStepVerifier() {
        StepVerifier
                .create(Mono.just("Hello Reactor")) // 테스트 대상 Sequence 생성
                .expectNext("Hello Reactor") // emit 된 데이터 기댓값 평가
                //.expectNext("Helo Reactor") // emit 된 데이터 기댓값 평가
                .expectComplete() // onComplete Signal 기댓값 평가
                .verify(); // 검증 실행
    }

    @Test
    void testWithStepVerifier1() {
        StepVerifier
                .create(GeneralTestExample.sayHello())
                .expectSubscription()
                .as("# expect Susbscription")
                .expectNext("Hi") // 실패함. Hi 가 아니라 Hello 이기 때문
                .as("# expect Hi")
                .expectNext("Reactor")
                .as("# expect Reactor")
                .verifyComplete();
    }

    @Test
    void testWithStepVerifier2() {
        Flux<Integer> source = Flux.just(2, 4, 6, 8, 10);
        StepVerifier
                .create(GeneralTestExample.divideByTwo(source))
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                //.expectNext(1, 2, 3, 4) // 한 번에 모두 테스트도 가능하다.
                .expectError()
                .verify();
    }

    @Test
    void testWithStepVerifier3() {
        Flux<Integer> source = Flux.range(0, 1000);
        StepVerifier
                .create(GeneralTestExample.takeNumber(source, 500),
                        StepVerifierOptions.create().scenarioName("Verify from 0 to 499") // 테스트에 실패할 경우, 파라미터로 입력한 시나리오 명을 출력
                )
                .expectSubscription()
                .expectNext(0)
                .expectNextCount(498)
                .expectNext(500) // 500개이지만 0부터 시작하므로 500이 되는 일은 없음 (499까지). 따라서 실패함
                .expectComplete()
                .verify();
    }

    public static class GeneralTestExample {
        public static Flux<String> sayHello() {
            return Flux
                    .just("Hello", "Reactor");
        }

        public static Flux<Integer> divideByTwo(Flux<Integer> source) {
            return source
                    .zipWith(Flux.just(2, 2, 2, 2, 0), (x, y) -> x / y);
        }

        public static Flux<Integer> takeNumber(Flux<Integer> source, long n) {
            return source
                    .take(n);
        }
    }
}
