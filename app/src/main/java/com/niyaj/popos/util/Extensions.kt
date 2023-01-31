package com.niyaj.popos.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat.getSystemService
import com.niyaj.popos.util.Constants.PRODUCT_NAME_LENGTH
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import java.util.Locale
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

val String.getFormattedDate
    get() = SimpleDateFormat(
        "dd-MM-yyyy HH:mm:ss",
        Locale.getDefault()
    ).format(this.toLong()).toString()

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

val String.toFullMonth
    get() = SimpleDateFormat("MMMM", Locale.getDefault()).format(this.toLong()).toString()

val String.toShortMonth
    get() = SimpleDateFormat("MMM", Locale.getDefault()).format(this.toLong()).toString()

val String.toFullYear
    get() = SimpleDateFormat("yyyy", Locale.getDefault()).format(this.toLong()).toString()

val String.toShortYear
    get() = SimpleDateFormat("yy", Locale.getDefault()).format(this.toLong()).toString()

val String.toMonthYear
    get() = SimpleDateFormat("MMMM yy", Locale.getDefault()).format(this.toLong()).toString()

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

internal fun isValidPassword(password: String): Boolean {
    if (password.length < 8) return false
    if (password.firstOrNull { it.isDigit() } == null) return false
    if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return false
    if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return false
    if (password.firstOrNull { !it.isLetterOrDigit() } == null) return false

    return true
}

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

private fun getLastWeekDays(date: String = ""): KotlinCollectionsList<Pair<String, String>> {
    val daysList = mutableListOf<Pair<String, String>>()

    for (i in 0 until 7) {
        val startTime = getCalculatedStartDate(if (i == 0) "0" else "-$i", date)
        val endTime = getCalculatedEndDate(if (i == 0) "0" else "-$i", date)

        daysList.add(Pair(startTime, endTime))
    }

    return daysList.toList()
}

private fun getLastSevenDaysStartAndEndDate(): Pair<String, String> {
    val startDate = getCalculatedStartDate("-7")
    val endDate = getCalculatedEndDate()

    return Pair(startDate, endDate)
}

private fun getNextMonthStartAndEndDate(date: String): Pair<String, String> {
    val getOnlyDate = SimpleDateFormat("dd", Locale.getDefault())
    val newDate = getOnlyDate.format(date.toLong()).toInt()

    val calendar = Calendar.getInstance()
    calendar.set(5, newDate)
    calendar[Calendar.HOUR_OF_DAY] = 23
    calendar[Calendar.MINUTE] = 59
    calendar[Calendar.SECOND] = 59

    val endCalendar = Calendar.getInstance()
    endCalendar.set(2, endCalendar.get(2).minus(1))
    endCalendar.set(5, newDate)
    endCalendar[Calendar.HOUR_OF_DAY] = 0
    endCalendar[Calendar.MINUTE] = 0
    endCalendar[Calendar.SECOND] = 0


    return Pair(endCalendar.timeInMillis.toString(), calendar.timeInMillis.toString())
}

private fun getSalaryCalculableDate(joinedDate: String): Pair<String, String> {
    val getOnlyDate = SimpleDateFormat("dd", Locale.getDefault())
    val newDate = getOnlyDate.format(joinedDate.toLong()).toInt()

    val startCalender = Calendar.getInstance()
    startCalender.set(2, startCalender.get(2).minus(1))
    startCalender.set(5, newDate)
    startCalender[Calendar.HOUR_OF_DAY] = 0
    startCalender[Calendar.MINUTE] = 0
    startCalender[Calendar.SECOND] = 0

    val endCalender = Calendar.getInstance()
    endCalender.set(5, newDate)
    endCalender[Calendar.HOUR_OF_DAY] = 23
    endCalender[Calendar.MINUTE] = 59
    endCalender[Calendar.SECOND] = 59

    return Pair(startCalender.timeInMillis.toString(), endCalender.timeInMillis.toString())
}

fun getSalaryDates(joinedDate: String): KotlinCollectionsList<Pair<String, String>> {

    val salaryDates = mutableListOf<Pair<String, String>>()

    val formatDate = SimpleDateFormat("dd", Locale.getDefault())
    val formattedDate = formatDate.format(joinedDate.toLong()).toInt()

    val currentYearAndMonth = YearMonth.now()

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

    return salaryDates
}

private fun getStartAndEndDate(
    date: Int,
    currentMonth: Int,
    currentYear: Int,
    previousMonth: Int,
    previousYear: Int
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

private fun localDateToCurrentMillis(date: LocalDate): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val calendar = Calendar.getInstance()
    calendar.time = dateFormat.parse(date.toString()) as Date

    return calendar.timeInMillis.toString()
}

private fun compareSalaryDates(joinedDate: String, comparableDate: String): Boolean {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val firstDate = sdf.parse(joinedDate.toSalaryDate) as Date
    val secondDate = sdf.parse(comparableDate.toSalaryDate) as Date

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

private fun setTodayStartTime(time: Int = 11): Calendar {
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