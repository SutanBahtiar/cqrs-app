package dev.natus.cqrs;

import dev.natus.cqrs.accountservice.command.aggregate.AccountAggregate;
import dev.natus.cqrs.common.command.CreateAccountCommand;
import dev.natus.cqrs.common.event.AccountCreatedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;

public class AccountTest {

    private static FixtureConfiguration<AccountAggregate> fixture;

    //    @BeforeAll
    public static void setUp() {
        fixture = new AggregateTestFixture<>(AccountAggregate.class);
    }

    //    @Test
    public void testCreateAccountCommand() {
        fixture.given(AccountCreatedEvent.builder()
                        .accountId("accountId")
                        .refId("refId")
                        .build())
                .when(CreateAccountCommand.builder()
                        .accountId("accountId")
                        .refId("refId")
                        .build())
                .expectSuccessfulHandlerExecution();
    }
}
