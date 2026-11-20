package abc.abc007


import java.util.ArrayDeque

enum class Plan(
    val dayRate: Long,
    val nightRate: Long
) {
    ONE_DRINK(100, 400),
    FREE_REFILLS(200, 500),
    ALCOHOL_FREE_REFILLS(300, 650);

    companion object {
        fun parse(value: String): Plan =
            when (value.lowercase()) {
                "one_drink" -> ONE_DRINK
                "free_refills" -> FREE_REFILLS
                "alcohol_free_refills" -> ALCOHOL_FREE_REFILLS
                else -> throw IllegalArgumentException("Unknown plan")
            }
    }
}

enum class DrinkType {
    SOFT_DRINK,
    ALCOHOL
}

data class Admission(
    val enteredAt: Int,
    var people: Int
)

data class Order(
    val unitPrice: Long,
    val quantity: Int
) {
    init {
        require(unitPrice >= 0)
        require(quantity > 0)
    }

    val total: Long
        get() = Math.multiplyExact(unitPrice, quantity.toLong())
}

object BillingCalculator {
    private const val BLOCK_SECONDS = 30 * 60
    private const val GRACE_SECONDS = 10 * 60

    fun calculate(
        plan: Plan,
        enteredAt: Int,
        leftAt: Int,
        people: Int
    ): Long {
        require(leftAt >= enteredAt)
        require(people > 0)

        val duration = leftAt - enteredAt
        val extra = (duration - GRACE_SECONDS).coerceAtLeast(0)

        val blocks = maxOf(
            1,
            (extra + BLOCK_SECONDS - 1) / BLOCK_SECONDS
        )

        val nightBoundary = if (enteredAt < 17 * 3600) {
            18 * 3600 + 50 * 60
        } else {
            17 * 3600 + 50 * 60
        }

        var price = 0L

        for (i in 0 until blocks) {
            val blockStart = enteredAt + i * BLOCK_SECONDS

            price = Math.addExact(
                price,
                if (blockStart >= nightBoundary) {
                    plan.nightRate
                } else {
                    plan.dayRate
                }
            )
        }

        return Math.multiplyExact(price, people.toLong())
    }
}

class ShopService(private val plan: Plan) {
    private val admissions = ArrayDeque<Admission>()

    private var totalEntered = 0
    private var totalDrinks = 0
    private var totalCost = 0L
    private var lastEventTime = -1

    fun advanceTime(time: Int) {
        require(time >= lastEventTime) {
            "Events must be chronological"
        }
        lastEventTime = time
    }

    fun enter(time: Int, people: Int) {
        require(people > 0)

        admissions.addLast(Admission(time, people))
        totalEntered = Math.addExact(totalEntered, people)
    }

    fun leave(time: Int, people: Int) {
        require(people > 0)
        require(people <= admissions.sumOf { it.people }) {
            "Not enough customers"
        }

        var remaining = people
        var charge = 0L

        while (remaining > 0) {
            val admission = admissions.first
            val leaving = minOf(remaining, admission.people)

            charge = Math.addExact(
                charge,
                BillingCalculator.calculate(
                    plan,
                    admission.enteredAt,
                    time,
                    leaving
                )
            )

            admission.people -= leaving
            remaining -= leaving

            if (admission.people == 0) {
                admissions.removeFirst()
            }
        }

        totalCost = Math.addExact(totalCost, charge)
    }

    fun orderDrink(type: DrinkType, order: Order) {
        totalDrinks = Math.addExact(totalDrinks, order.quantity)
        totalCost = Math.addExact(totalCost, order.total)
    }

    fun orderFood(order: Order) {
        totalCost = Math.addExact(totalCost, order.total)
    }

    fun finish(): Long {
        require(admissions.isEmpty()) {
            "Some customers have not left"
        }

        if (plan == Plan.ONE_DRINK) {
            require(totalDrinks >= totalEntered) {
                "One drink per customer is required"
            }
        }

        return totalCost
    }
}

object CommandParser {
    private val timePattern =
        Regex("""(\d{1,2}):([0-5]\d):([0-5]\d)""")

    fun parseTime(value: String): Int {
        val match = timePattern.matchEntire(value)
            ?: throw IllegalArgumentException("Invalid time")

        val (hour, minute, second) =
            match.destructured.toList().map { it.toInt() }

        require(hour in 8..31)

        return hour * 3600 + minute * 60 + second
    }

    fun execute(service: ShopService, line: String) {
        val parts = line.trim().split(Regex("""\s+"""))
        require(parts.size >= 2)

        val time = parseTime(parts[0])
        val command = parts[1]

        fun checkSize(size: Int) {
            require(parts.size == size) {
                "Invalid arguments for $command"
            }
        }

        service.advanceTime(time)

        when (command) {
            "enter" -> {
                checkSize(3)
                service.enter(time, parts[2].toInt())
            }

            "leave" -> {
                checkSize(3)
                service.leave(time, parts[2].toInt())
            }

            "drink" -> {
                checkSize(5)

                val type = when (parts[2]) {
                    "soft_drink" -> DrinkType.SOFT_DRINK
                    "alcohol" -> DrinkType.ALCOHOL
                    else -> throw IllegalArgumentException(
                        "Invalid drink type"
                    )
                }

                service.orderDrink(
                    type,
                    Order(parts[3].toLong(), parts[4].toInt())
                )
            }

            "food" -> {
                checkSize(4)
                service.orderFood(
                    Order(parts[2].toLong(), parts[3].toInt())
                )
            }

            else -> throw IllegalArgumentException("Unknown command")
        }
    }
}

fun main() {
    try {
        val plan = Plan.parse(readln().trim())
        val service = ShopService(plan)

        while (true) {
            val line = readlnOrNull() ?: break

            if (line.isBlank()) continue
            if (line.trim() == "finish") break

            CommandParser.execute(service, line)
        }

        println("""{"code":999,"cost":${service.finish()}}""")
    } catch (e: IllegalArgumentException) {
        println("""{"code":0}""")
    } catch (e: ArithmeticException) {
        println("""{"code":0}""")
    }
}
