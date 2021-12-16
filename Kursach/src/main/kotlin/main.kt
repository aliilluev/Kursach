import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow


class Audience() {
    var Name: String = ""
    var Coord: ArrayList<Int> = ArrayList()
    var Points: ArrayList<Int> = ArrayList()

    fun Add() {
        println("Введите название аудитории")
        //Name = readLine().toString()
        val scan = Scanner(System.`in`)
        Name = scan.nextLine()
        for (i in 0..1) {
            println("Введите ${i+1}ю координату")
            Coord.add(scan.nextInt())
        }

        println("Введите ближайшие аудитории")
        Points.add(scan.nextInt())
        Points.add(scan.nextInt())
        Points.sort()
    }

    fun ToConsole () {
        println(Name)
        println(Coord)
        println(Points)
    }

    fun ToFile (writer: BufferedWriter) {
        writer.write("$Name\n")
        writer.write("$Coord\n")
        writer.write("$Points\n")
    }

    fun FromFile (reader: BufferedReader) {
        Name = reader.readLine()
        val coord = reader.readLine().split(", ","[","]")
        for (i in coord) {
            val j = i.toIntOrNull()
            if (j != null) {
                Coord.add(j)
            }
        }
        val points = reader.readLine().split(", ","[","]")
        for (i in points) {
            val j = i.toIntOrNull()
            if (j != null) {
                Points.add(j)
            }
        }
    }
}

fun AudsFromFile(): ArrayList<Audience> {
    val Auds: ArrayList<Audience> = ArrayList()
    val inputName = "base.txt"
    val reader = File(inputName).bufferedReader()
    while (true) {
        val Aud = Audience()
        val ints: ArrayList<Int> = ArrayList()
        Aud.Name = reader.readLine() ?: break
        val coord = reader.readLine().split(", ","[","]")
        for (i in coord) {
            val j = i.toIntOrNull()
            if (j != null) {
                Aud.Coord.add(j)
            }
        }
        val points = reader.readLine().split(", ","[","]")
        for (i in points) {
            val j = i.toIntOrNull()
            if (j != null) {
                Aud.Points.add(j)
            }
        }
        Auds.add(Aud)
    }
    reader.close()
    return Auds
}

fun norm(A: ArrayList<Int>, B: ArrayList<Int>): Double{
    val Dems = A.size-1
    var res = 0.0
    for (i in 0..Dems) {
        res += (A[i]-B[i]).toDouble().pow(2.0)
    }
    res = res.pow(0.5)
    return res
}

fun Draw_Way(WayVect: ArrayList<ArrayList<Int>>): String {
    var Way = ""
    for (i in 0 until WayVect.size) {
        Way += if (i == 0) {
            "M"
        } else {
            " L"
        }
        for (j in 0 until WayVect[i].size) {
            if (j != 0) {
                Way += ","
            }
            Way += "${WayVect[i][j]}"
        }
    }
    return Way
}

fun Draw_Flag(EndCoord: ArrayList<Int>): String {
    val cX = EndCoord[0] - 120
    val cY = EndCoord[1] - 220
    val Flag = "M${120+cX},${20+cY}" +
            "C${81.3+cX},${20+cY} ${50+cX},${51.3+cY}" +
            " ${50+cX},${90+cY}c${0},${52.5} ${70},${130} ${70},${130}" +
            "s${70},${-77.5} ${70},${-130}c${0},${-38.7}" +
            " ${-31.3},${-70} ${-70},${-70}z" +
            "M${120+cX},${115+cY}" +
            "c${-13.8},${0} ${-25},${-11.2}" +
            " ${-25},${-25}s${11.2},${-25}" +
            " ${25},${-25} ${25},${11.2} ${25},${25}" +
            " ${-11.2},${25} ${-25},${25}z"
    return Flag
}

fun Make_xml(WayVect: ArrayList<ArrayList<Int>>) {
    val WayString = Draw_Way(WayVect)
    val EndNum = WayVect.size - 1
    val EndFlag = Draw_Flag(WayVect[EndNum])

    println(WayString)
    val outputName = "route.xml"
    val writer = File(outputName).bufferedWriter()
    writer.write("<vector xmlns:android=\"http://schemas.android.com/apk/res/android\"")
    writer.write("\n    android:width=\"3840dp\"")
    writer.write("\n    android:height=\"2160dp\"")
    writer.write("\n    android:viewportWidth=\"3840\"")
    writer.write("\n    android:viewportHeight=\"2150\">")
    writer.write("\n    <path")
    writer.write("\n        android:pathData = \"${WayString}\"")
    writer.write("\n        android:strokeLineCap=\"round\"")
    writer.write("\n        android:strokeLineJoin=\"round\"")
    writer.write("\n        android:strokeWidth = \"20\"")
    writer.write("\n        android:strokeColor = \"@color/route_color\"/>")
    writer.write("\n    <path")
    writer.write("\n        android:fillColor=\"@android:color/holo_red_dark\"")
    writer.write("\n        android:pathData=\"${EndFlag}\"/>")
    writer.write("\n</vector>")
    writer.close()
}

class Graph() {
    var Name: String = ""
    var WeightMatr: ArrayList<ArrayList<Double>> = ArrayList(ArrayList())
    var PointsMatr: ArrayList<ArrayList<Int>> = ArrayList(ArrayList())
    var Coords: ArrayList<ArrayList<Int>> = ArrayList(ArrayList())

    fun Floyd(){
        val MatrSize = WeightMatr.size-1
        for (k in 0..MatrSize){
            for (i in 0..MatrSize){
                if (i != k) {
                    for (j in 0..MatrSize){
                        if (j != k) {
                            val NewElement = WeightMatr[i][k]+WeightMatr[k][j]
                            if (NewElement < WeightMatr[i][j]) {
                                WeightMatr[i][j] = NewElement
                                PointsMatr[i][j] = PointsMatr[k][j]
                            }
                        }
                    }
                }
            }
        }
    }

    private fun ReverseFloyd(StartInd: Int, EndInd: Int): ArrayList<Int> {
        val res: ArrayList<Int> = ArrayList()
        var i = EndInd;
        res.add(i)
        while (i != StartInd) {
            i = PointsMatr[StartInd][i]
            res.add(i)
        }
        return res
    }

    fun Way(StartPt: Audience, EndPt: Audience): ArrayList<ArrayList<Int>> {
        val res: ArrayList<ArrayList<Int>> = ArrayList(ArrayList())
        res.add(StartPt.Coord)
        val Indices: ArrayList<Int>
        var StartInd = 0
        var EndInd = 0
        if (StartPt.Points != EndPt.Points) {
            var Distance = 999999.9
            for (i in StartPt.Points) {
                for (j in EndPt.Points) {
                    val DistStart = norm(StartPt.Coord, Coords[i])*
                            WeightMatr[StartPt.Points[0]][StartPt.Points[1]]/
                            norm(Coords[StartPt.Points[0]], Coords[StartPt.Points[1]])
                    val DistEnd = norm(EndPt.Coord, Coords[j])*
                            WeightMatr[EndPt.Points[0]][EndPt.Points[1]]/
                            norm(Coords[EndPt.Points[0]], Coords[EndPt.Points[1]])
                    val Start_End = DistStart + WeightMatr[i][j] + DistEnd
                    if (Distance > Start_End) {
                        Distance = Start_End
                        StartInd = i
                        EndInd = j
                    }
                }
            }
            Indices = ReverseFloyd(StartInd, EndInd)
            Indices.reverse()
            for (i in Indices) {
                res.add(Coords[i])
            }
        }

        res.add(EndPt.Coord)
        return res
    }

    fun KeyPoints() {
        Coords.add(arrayListOf(851,484))
        Coords.add(arrayListOf(851,950))
        Coords.add(arrayListOf(851,1343))
        Coords.add(arrayListOf(1860,950))
        Coords.add(arrayListOf(2964,950))
        Coords.add(arrayListOf(2964,473))
        Coords.add(arrayListOf(2964,1310))
        Coords.add(arrayListOf(3715,1310))
        Coords.add(arrayListOf(1860,1870))
        Coords.add(arrayListOf(222,1343))
    }

    fun Base() {
        val MatrSize = 9;
        for (i in 0..MatrSize) {
            val Vect: ArrayList<Int> = ArrayList()
            for (j in 0..MatrSize) {
                Vect.add(i)
            }
            PointsMatr.add(Vect)
        }
        val Inf = 999999.9
        WeightMatr.add(arrayListOf(0.0, 10.0, Inf, Inf, Inf, Inf, Inf, Inf, Inf, Inf))
        WeightMatr.add(arrayListOf(10.0, 0.0, 7.0, 15.0, Inf, Inf, Inf, Inf, Inf, Inf))
        WeightMatr.add(arrayListOf(Inf, 7.0, 0.0, Inf, Inf, Inf, Inf, Inf, Inf, 10.0))
        WeightMatr.add(arrayListOf(Inf, 15.0, Inf, 0.0, 15.0, Inf, Inf, Inf, 12.0, Inf))
        WeightMatr.add(arrayListOf(Inf, Inf, Inf, 15.0, 0.0, 10.0, 7.0, Inf, Inf, Inf))
        WeightMatr.add(arrayListOf(Inf, Inf, Inf, Inf, 10.0, 0.0, Inf, Inf, Inf, Inf))
        WeightMatr.add(arrayListOf(Inf, Inf, Inf, Inf, 7.0, Inf, 0.0, 10.0, Inf, Inf))
        WeightMatr.add(arrayListOf(Inf, Inf, Inf, Inf, Inf, Inf, 10.0, 0.0, Inf, Inf))
        WeightMatr.add(arrayListOf(Inf, Inf, Inf, 12.0, Inf, Inf, Inf, Inf, 0.0, Inf))
        WeightMatr.add(arrayListOf(Inf, Inf, 10.0, Inf, Inf, Inf, Inf, Inf, Inf, 0.0))
    }

    fun ToConsole () {
        for (i in Coords) {
            println(i)
        }
        val MatrSize = PointsMatr.size-1
        for (i in 0..MatrSize) {
            print(PointsMatr[i])
            println(WeightMatr[i])
        }
    }

    fun CoordsToFile () {
        val outputName = "coords.txt"
        val writer = File(outputName).bufferedWriter()
        for (i in Coords) {
            writer.write("$i\n")
        }
        writer.close()
    }

    fun CoordsFromFile () {
        val inputName = "coords.txt"
        val reader = File(inputName).bufferedReader()
        while (true) {
            val ints: ArrayList<Int> = ArrayList()
            val str = reader.readLine() ?: break
            val coord = str.split(", ","[","]")
            for (i in coord) {
                val j = i.toIntOrNull()
                if (j != null) {
                    ints.add(j)
                }
            }
            Coords.add(ints)
        }
        reader.close()
    }

    fun PointsToFile () {
        val outputName = "points.txt"
        val writer = File(outputName).bufferedWriter()
        for (i in PointsMatr) {
            writer.write("$i\n")
        }
        writer.close()
    }

    fun PointsFromFile () {
        val inputName = "points.txt"
        val reader = File(inputName).bufferedReader()
        while (true) {
            val ints: ArrayList<Int> = ArrayList()
            val str = reader.readLine() ?: break
            val points = str.split(", ","[","]")
            for (i in points) {
                val j = i.toIntOrNull()
                if (j != null) {
                    ints.add(j)
                }
            }
            PointsMatr.add(ints)
        }
        reader.close()
    }

    fun WeightsToFile () {
        val outputName = "weights.txt"
        val writer = File(outputName).bufferedWriter()
        for (i in WeightMatr) {
            writer.write("$i\n")
        }
        writer.close()
    }

    fun WeightsFromFile () {
        val inputName = "weights.txt"
        val reader = File(inputName).bufferedReader()
        while (true) {
            val ints: ArrayList<Double> = ArrayList()
            val str = reader.readLine() ?: break
            val weights = str.split(", ","[","]")
            for (i in weights) {
                val j = i.toDoubleOrNull()
                if (j != null) {
                    ints.add(j)
                }
            }
            WeightMatr.add(ints)
        }
        reader.close()
    }
}

fun Find(Audiences: ArrayList<Audience>): Audience {
    var ind: Boolean
    ind = false
    var Aud = Audience()
    while (true) {
        val Aud_Name = readLine().toString()
        for (i in Audiences) {
            if (Aud_Name == i.Name) {
                Aud = i
                ind = true
            }
        }
        if (ind) {
            break
        } else {
            println("Неправильно введена аудитория")
        }
    }
    return Aud
}

fun TestAuds(): ArrayList<Audience> {
    val Auds: ArrayList<Audience> = ArrayList()

    for (i in 0..0) {
        val Aud = Audience()
        Aud.Name = "125"
        Aud.Coord = arrayListOf(1297, 950)
        Aud.Points = arrayListOf(1, 3)
        Auds.add(Aud)
    }

    for (i in 0..0) {
        val Aud = Audience()
        Aud.Name = "153"
        Aud.Coord = arrayListOf(3433, 1310)
        Aud.Points = arrayListOf(6, 7)
        Auds.add(Aud)
    }

    for (i in 0..0) {
        val Aud = Audience()
        Aud.Name = "145"
        Aud.Coord = arrayListOf(1596, 950)
        Aud.Points = arrayListOf(1, 3)
        Auds.add(Aud)
    }

    for (i in 0..0) {
        val Aud = Audience()
        Aud.Name = "Exit"
        Aud.Coord = arrayListOf(1860,1870)
        Aud.Points = arrayListOf(3, 8)
        Auds.add(Aud)
    }

    return Auds
}

fun start1() {
    val Level_1 = Graph()
    Level_1.Name = "First Level"

    var Audiences: ArrayList<Audience> = ArrayList()

    Audiences = TestAuds()

    Level_1.KeyPoints()

    Level_1.Base()

    Level_1.ToConsole()

    println()

    Level_1.Floyd()

    Level_1.ToConsole()

    Level_1.CoordsToFile()
    Level_1.PointsToFile()
    Level_1.WeightsToFile()

    println()

    println("Введите начальную аудиторию")
    val Start_Aud: Audience = Find(Audiences)

    println("Введите конечную аудиторию")
    val End_Aud: Audience = Find(Audiences)

    println()
    val WayVect = Level_1.Way(Start_Aud,End_Aud)
    for (i in WayVect) {
        println(i)
    }
    println()

    for (i in Audiences) {
        i.ToConsole()
    }

    Audiences.removeAt(2)

    for (i in Audiences) {
        i.ToConsole()
    }

    Make_xml(WayVect)
}

fun start2() {
    val Level_1 = Graph()
    Level_1.Name = "First Level"

    var Audiences: ArrayList<Audience> = ArrayList()

    Audiences = AudsFromFile()
    Level_1.CoordsFromFile()
    Level_1.PointsFromFile()
    Level_1.WeightsFromFile()

    while (true) {
        println("Введите начальную аудиторию")
        val Start_Aud: Audience = Find(Audiences)

        println("Введите конечную аудиторию")
        val End_Aud: Audience = Find(Audiences)

        println()
        val WayVect = Level_1.Way(Start_Aud,End_Aud)
        for (i in WayVect) {
            println(i)
        }
        println()

        Make_xml(WayVect)
    }
}

fun main(args: Array<String>) {
    //start1()
    start2()
    //start3()
}