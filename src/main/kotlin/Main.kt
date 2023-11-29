package application

import java.io.File
import java.util.*

fun main() {
    val file = Scanner(File("src/main/test.txt"))
    val algoritimo = file.nextLine()
    var faults = 0
    val size = file.nextInt()
    file.nextLine()
    println(algoritimo)
    when (algoritimo) {
        "FIFO" -> {
            faults = fifo(file, size)
        }

        "SC" -> {
            faults = sc(file, size)
        }

        "CLOCK" -> {
            faults = clock(file, size)
        }

        else -> {
            println("não suportado")
        }
    }

    println("Total Page Fault = $faults")
}


class Quadro(val id: Int) {
    var secondChance = 0

    fun addSecondChance() {
        secondChance = 1
    }

    fun removeSecondChance() {
        secondChance = 0
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Quadro) {
            this.id == other.id
        } else false
    }

    override fun toString(): String {
        return id.toString()
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + secondChance
        return result
    }
}


fun fifo(file: Scanner, size: Int): Int {
    var faults = 0
    val fila = LinkedList<Quadro>()
    while (file.hasNextLine()) {
        val id = file.nextInt()
        val q = Quadro(id)
        file.nextLine()
        if (!fila.contains(q)) {
            println("Page Fault - Página $q")

            if (fila.size == size) {
                fila.remove()
            }
            fila.add(q)
            faults++
        } else {
            println("Page Hit - Página $q")
        }
    }

    return faults
}


fun sc(file: Scanner, size: Int): Int {
    var faults = 0
    val fila = LinkedList<Quadro>()
    var q: Quadro
    while (file.hasNextLine()) {
        val id = file.nextInt()
        q = Quadro(id)
        file.nextLine()
        if (!fila.contains(q)) {
            while (fila.size == size) {
                var rmQ = fila.remove()
                if (rmQ.secondChance == 1) {
                    println("id: $rmQ used your second chance")
                    rmQ = Quadro(rmQ.id)
                    fila.add(rmQ)
                }
            }

            println("Page Fault - Página $q")

            fila.add(q)
            faults++
        } else {
            val fQ = q
            fila.map { p ->
                if (p.id == fQ.id) {
                    println("id: $p win second chance")
                    p.addSecondChance()
                }
                p
            }
            println("Page Hit - Página $q")
        }

        for (el in fila) {
            println("id: $el")
        }
    }

    return faults
}


fun clock(file: Scanner, size: Int): Int {
    var faults = 0
    val lista = mutableListOf<Quadro>()
    var clock = 0
    while (file.hasNextLine()) {
        val id = file.nextInt()
        val q = Quadro(id)
        file.nextLine()

        var inside: Boolean
        if (!lista.contains(q)) {
            if (lista.size == size) {
                inside = false
                while (!inside) {
                    val rmQ = lista[clock % size]
                    if (rmQ.secondChance == 1) {
                        println("id: $rmQ used your second chance")
                        lista.map { p ->
                            if (p.id == rmQ.id) {
                                println("id: $p win second chance")
                                p.removeSecondChance()
                            }
                            p
                        }
                    } else {
                        lista.removeAt(clock % size)
                        lista.add(clock % size, Quadro(id))
                        inside = true
                    }
                    clock += 1
                }
            } else {
                lista.add(q)
            }

            println("Page Fault - Página $q")
            println("Clock: $clock")
            faults++
            clock++
        } else {
            println("Page Hit - Página $q")
            println("Clock: $clock")
            lista.map { p ->
                if (p.id == q.id) {
                    p.addSecondChance()
                }
                p
            }
        }
    }
    return faults
}
