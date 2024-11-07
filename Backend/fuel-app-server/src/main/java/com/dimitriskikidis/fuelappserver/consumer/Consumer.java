package com.dimitriskikidis.fuelappserver.consumer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "Consumer")
@Table(
        name = "consumers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UQ_Consumers_UserId",
                        columnNames = "userId"
                ),
                @UniqueConstraint(
                        name = "UQ_Consumers_Username",
                        columnNames = "username"
                )
        }
)
public class Consumer {

    @Id
    @SequenceGenerator(
            name = "consumers_id_sequence",
            sequenceName = "consumers_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "consumers_id_sequence"
    )
    private Integer id;

    @Column(
            nullable = false
    )
    private Integer userId;

    @Column(
            nullable = false,
            length = 50
    )
    private String username;

    public Consumer(Integer userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consumer consumer = (Consumer) o;
        return id != null && id.equals(consumer.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
