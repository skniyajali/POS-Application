package com.niyaj.popos.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat.getSystemService
import com.niyaj.popos.util.Constants.PRODUCT_NAME_LENGTH
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.collections.List as KotlinCollectionsList


val randomColor: Int
    get() {
        return Color.rgb((30..200).random(),(30..200).random(),(30..200).random())
    }


val String.isContainsArithmeticCharacter: Boolean
    get() = this.any { str ->
        (str == '%' || str == '/' || str == '*' || str == '+' || str == '-')
    }

val String.capitalizeWords
    get() = this.lowercase(Locale.ROOT).split(" ").joinToString(" ") { char ->
        char.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        }
    }

fun getAllCapitalizedLetters(string: String): String {
    var capitalizeLetters = ""

    string.capitalizeWords.forEach {
        if (it.isUpperCase()) {
            capitalizeLetters += it.toString()
        }
    }

    return capitalizeLetters
}

val String.toFormattedDateAndTime
    get() = SimpleDateFormat(
        "dd MMM, hh:mm a",
        Locale.getDefault()
    ).format(this.toLong()).toString()

val String.toFormattedTime
    get() = SimpleDateFormat(
        "hh:mm a",
        Locale.getDefault()
    ).format(this.toLong()).toString()

val String.toFormattedDate
    get() = SimpleDateFormat(
        "dd MMM yy",
        Locale.getDefault()
    ).format(this.toLong()).toString()

val String.toBarDate
    get() = SimpleDateFormat(
        "dd MMM",
        Locale.getDefault()
    ).format(this.toLong()).toString()

val String.toYearAndMonth
    get() = SimpleDateFormat(
        "MMM yyyy",
        Locale.getDefault()
    ).format(this.toLong()).toString()

val String.toDate
    get() = SimpleDateFormat("dd", Locale.getDefault()).format(this.toLong()).toString()

val String.toTime
    get() = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(this.toLong()).toString()


val zoneId: ZoneId = ZoneId.of("Asia/Kolkata")

val LocalDate.toMilliSecond: String
    get() = this.atStartOfDay(zoneId)
        .toLocalDateTime()
        .atZone(zoneId)
        .toInstant().toEpochMilli()
        .toString()

val LocalDate.toCurrentMilliSecond: String
    get() = this.atTime(LocalTime.now().hour, LocalTime.now().minute)
        .atZone(zoneId)
        .toInstant()
        .toEpochMilli()
        .toString()

val String.toSalaryDate
    get() = SimpleDateFormat(
        "dd-MM-yyyy",
        Locale.getDefault()
    ).format(this.toLong()).toString()

fun startTime(): Calendar {
    val startTime = Calendar.getInstance()
    startTime[Calendar.HOUR_OF_DAY] = 0
    startTime[Calendar.MINUTE] = 0
    startTime[Calendar.SECOND] = 0
    startTime[Calendar.MILLISECOND] = 0

    return startTime
}

fun endTime(): Calendar {
    val endTime = startTime().clone() as Calendar
    endTime[Calendar.HOUR_OF_DAY] = 23
    endTime[Calendar.MINUTE] = 59
    endTime[Calendar.SECOND] = 59
    endTime[Calendar.MILLISECOND] = 0

    return endTime
}

val getStartTime: String = startTime().timeInMillis.toString()
val getEndTime: String = endTime().timeInMillis.toString()

val String.toRupee
    get() = DecimalFormat
        .getCurrencyInstance(Locale("en", "IN"))
        .format(this.toLong())
        .substringBefore(".")

fun getCalculatedStartDate(days: String = "", date: String = ""): String {
    val calendar = Calendar.getInstance()
    val s = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        if (date.isNotEmpty()) {
            calendar.time = s.parse(date) as Date
        }
    } catch (e: Exception) {
        calendar.timeInMillis = date.toLong()
    }

    val day = try {
        if (days.isNotEmpty()) days.toInt() else 0
    } catch (e: Exception) {
        0
    }
    calendar.add(Calendar.DAY_OF_YEAR, day)
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0

    return calendar.timeInMillis.toString()
}

fun getCalculatedEndDate(days: String = "", date: String = ""): String {
    val calendar = Calendar.getInstance()
    val s = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        if (date.isNotEmpty()) {
            calendar.time = s.parse(date) as Date
        }
    } catch (e: Exception) {
        calendar.timeInMillis = date.toLong()
    }

    val day = try {
        if (days.isNotEmpty()) days.toInt() else 0
    } catch (e: Exception) {
        0
    }
    calendar.add(Calendar.DAY_OF_YEAR, day)
    calendar[Calendar.HOUR_OF_DAY] = 23
    calendar[Calendar.MINUTE] = 59
    calendar[Calendar.SECOND] = 59
    calendar[Calendar.MILLISECOND] = 0

    return calendar.timeInMillis.toString()
}

fun getSalaryDates(joinedDate: String): KotlinCollectionsList<Pair<String, String>> {

    val currentYearAndMonth = YearMonth.now()

    val salaryDates = mutableListOf<Pair<String, String>>()

    val formatDate = SimpleDateFormat("dd", Locale.getDefault())
    val formattedDate = formatDate.format(joinedDate.toLong()).toInt()

    for (i in 0 until 5) {
        var previousMonth = 0
        var previousYear = 0

        val subtractMonth = currentYearAndMonth.minusMonths(i.toLong())

        for (j in 1 until 2) {
            previousMonth = subtractMonth.minusMonths(j.toLong()).month.value
            previousYear = subtractMonth.minusMonths(j.toLong()).year
        }

        val currentMonth = subtractMonth.month.value
        val currentYear = subtractMonth.year

        val comparePreDate = compareSalaryDates(
            getStartDate(formattedDate, previousMonth, previousYear),
            Calendar.getInstance().timeInMillis.toString()
        )

        if (comparePreDate) {
            salaryDates.add(
                getStartAndEndDate(
                    date = formattedDate,
                    currentMonth = currentMonth,
                    currentYear = currentYear,
                    previousMonth = previousMonth,
                    previousYear = previousYear
                )
            )
        }
    }

    return salaryDates
}

private fun getStartAndEndDate(
    date : Int,
    currentMonth : Int,
    currentYear : Int,
    previousMonth : Int,
    previousYear : Int
): Pair<String, String> {
    val startCalender = Calendar.getInstance()
    startCalender[Calendar.DATE] = date
    startCalender[Calendar.YEAR] = previousYear
    startCalender[Calendar.MONTH] = previousMonth
    startCalender[Calendar.HOUR_OF_DAY] = 0
    startCalender[Calendar.MINUTE] = 0
    startCalender[Calendar.SECOND] = 0
    startCalender[Calendar.MILLISECOND] = 0


    val endCalender = Calendar.getInstance()
    endCalender[Calendar.DATE] = date
    endCalender[Calendar.YEAR] = currentYear
    endCalender[Calendar.MONTH] = currentMonth
    endCalender[Calendar.HOUR_OF_DAY] = 23
    endCalender[Calendar.MINUTE] = 59
    endCalender[Calendar.SECOND] = 59

    return Pair(startCalender.timeInMillis.toString(), endCalender.timeInMillis.toString())
}

private fun getStartDate(date : Int, currentMonth : Int, currentYear : Int): String {
    val startCalender = Calendar.getInstance()
    startCalender[Calendar.DATE] = date
    startCalender[Calendar.YEAR] = currentYear
    startCalender[Calendar.MONTH] = currentMonth
    startCalender[Calendar.HOUR_OF_DAY] = 0
    startCalender[Calendar.MINUTE] = 0
    startCalender[Calendar.SECOND] = 0
    startCalender[Calendar.MILLISECOND] = 0

    return startCalender.timeInMillis.toString()
}

fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
    val channel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_HIGH
    )

    getSystemService(context, NotificationManager::class.java)?.createNotificationChannel(channel)
}

fun formattedDateToStartMillis(formattedDate: String): String {
    val s = SimpleDateFormat("dd-MM-yyyy",Locale.getDefault())
    val calendar = Calendar.getInstance()

    try {
        if (formattedDate.isNotEmpty()) {
            calendar.time = s.parse(formattedDate) as Date
        }
    } catch (e: Exception) {
        calendar.timeInMillis = formattedDate.toLong()
    }

    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0

    return calendar.timeInMillis.toString()
}

fun compareSalaryDates(joinedDate: String, comparableDate: String): Boolean {
    val calendar = Calendar.getInstance()

    calendar.timeInMillis = joinedDate.toLong()
    val firstDate = calendar.time

    calendar.timeInMillis = comparableDate.toLong()
    val secondDate = calendar.time

    val cmp = firstDate.compareTo(secondDate)

    return when {
        cmp < 0 -> true
        cmp > 0 -> false
        else -> true
    }
}

fun toMonthAndYear(date: String): String {
    val currentYear = Year.now().value.toString()
    val format = SimpleDateFormat("yyyy", Locale.getDefault()).format(date.toLong()).toString()

    return if (currentYear == format) {
        SimpleDateFormat("MMMM", Locale.getDefault()).format(date.toLong()).toString()
    }else {
        SimpleDateFormat("MMMM yy", Locale.getDefault()).format(date.toLong()).toString()
    }
}

fun setTodayStartTime(time: Int = 1): Calendar {
    val startTime = Calendar.getInstance()
    startTime[Calendar.HOUR_OF_DAY] = time
    startTime[Calendar.MINUTE] = 0
    startTime[Calendar.SECOND] = 0
    startTime[Calendar.MILLISECOND] = 0

    return startTime
}

private fun setTodayEndTime(time: Int = 23): Calendar {
    val endTime = Calendar.getInstance()
    endTime[Calendar.HOUR_OF_DAY] = time
    endTime[Calendar.MINUTE] = 0
    endTime[Calendar.SECOND] = 0
    endTime[Calendar.MILLISECOND] = 0

    return endTime
}

val openingTime: String = setTodayStartTime().timeInMillis.toString()
val closingTime: String = setTodayEndTime().timeInMillis.toString()
val dailySalaryStartTime = setTodayStartTime(7).timeInMillis.toString()


fun createDottedString(name: String): String {
    if (name.length > PRODUCT_NAME_LENGTH) {
        var wordLength = 0
        var firstWordLength = 0
        val splitName = name.split(' ')

        splitName.forEachIndexed { index, word ->
            if (index != 0) {
                wordLength += word.length.plus(1)
            }else {
                firstWordLength = word.length
            }
        }

        val remainingLength = PRODUCT_NAME_LENGTH.minus(firstWordLength)

        val whiteSpace = splitName.size - 1

        val remLength = wordLength.plus(whiteSpace).minus(remainingLength).div(splitName.size.minus(1))

        var newName = ""

        splitName.forEachIndexed { index, name1 ->
            if (index != 0) {
                val wordLen = name1.length.minus(remLength.plus(1))
                val dottedName = if (wordLen <= 0) name1.substring(0, 1) else name1.substring(0, wordLen).plus(".")
                newName += " $dottedName"
            }else {
                newName = name1
            }
        }

        return newName
    } else return name
}

fun String.toPrettyDate(): String {
    val nowTime = Calendar.getInstance()
    val neededTime = Calendar.getInstance()
    neededTime.timeInMillis = this.toLong()

    return if (neededTime[Calendar.YEAR] == nowTime[Calendar.YEAR]) {
        if (neededTime[Calendar.MONTH] == nowTime[Calendar.MONTH]) {
            when {
                neededTime[Calendar.DATE] - nowTime[Calendar.DATE] == 1 -> {
                    //here return like "Tomorrow at 12:00"
                    "Tomorrow"
                }
                nowTime[Calendar.DATE] == neededTime[Calendar.DATE] -> {
                    //here return like "Today at 12:00"
                    "Today"
                }
                nowTime[Calendar.DATE] - neededTime[Calendar.DATE] == 1 -> {
                    //here return like "Yesterday at 12:00"
                    "Yesterday"
                }
                else -> {
                    //here return like "May 31, 12:00"
                    SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this.toLong()))
                }
            }
        } else {
            //here return like "May 31, 12:00"
            SimpleDateFormat("MMMM dd", Locale.getDefault()).format(Date(this.toLong()))
        }
    } else {
        //here return like "May 31 2022, 12:00" - it's a different year we need to show it
        SimpleDateFormat("MMMM dd yyyy", Locale.getDefault()).format(Date(this.toLong()))
    }
}

fun String.toDailySalaryAmount(): String {
    val dailyAmount = this.toLong().div(30).toInt()
    val numberFormat = NumberFormat.getInstance()
    numberFormat.roundingMode = RoundingMode.CEILING
    numberFormat.format(dailyAmount)

    return dailyAmount.toString().toRupee
}
