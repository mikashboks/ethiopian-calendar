package com.mkb.ethiopian.lib

import java.util.*

class EthiopicCalendar {

    private var jdOffset = JD_EPOCH_OFFSET_UNSET
    var year = -1
        private set
    var month = -1
        private set
    var day = -1
        private set
    private var dateIsUnset = true

    /*
     ** ********************************************************************************
     **  Constructors
     ** ********************************************************************************
     */
    constructor() {}
    constructor(cal: Calendar) {
        // you pass a calendar reference
        this[cal[Calendar.YEAR], cal[Calendar.MONTH] + 1] = cal[Calendar.DAY_OF_MONTH]
    }

    constructor(year: Int, month: Int, day: Int, era: Int) {
        // you pass
        this[year, month, day] = era
    }

    constructor(year: Int, month: Int, day: Int) {
        this[year, month] = day
    }

    /*
     ** ********************************************************************************
     **  Simple Setters and Getters
     ** ********************************************************************************
     */
    @Throws(ArithmeticException::class)
    operator fun set(year: Int, month: Int, day: Int, era: Int) {
        this.year = year
        this.month = month
        this.day = day
        this.era = era
        dateIsUnset = false
    }

    operator fun set(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
        dateIsUnset = false
    }

    @set:Throws(ArithmeticException::class)
    var era: Int
        get() = jdOffset
        set(era) {
            jdOffset = if (JD_EPOCH_OFFSET_AMETE_ALEM == era
                || JD_EPOCH_OFFSET_AMETE_MIHRET == era
            ) {
                era
            } else {
                throw ArithmeticException("Unknown era: $era must be either ዓ/ዓ or ዓ/ም.")
            }
        }
    val date: IntArray
        get() = intArrayOf(year, month, day, jdOffset)
    val isEraSet: Boolean
        get() = if (JD_EPOCH_OFFSET_UNSET == jdOffset) false else true

    fun unsetEra() {
        jdOffset = JD_EPOCH_OFFSET_UNSET
    }

    fun unset() {
        unsetEra()
        year = -1
        month = -1
        day = -1
        dateIsUnset = true
    }

    val isDateSet: Boolean
        get() = if (dateIsUnset) false else true

    /*
       ** ********************************************************************************
       **  Conversion Methods To/From the Ethiopic & Gregorian Calendars
       ** ********************************************************************************
       */
    @Throws(ArithmeticException::class)
    fun ethiopicToGregorian(era: Int): IntArray {
        if (!isDateSet) {
            throw ArithmeticException("Unset date.")
        }
        return ethiopicToGregorian(year, month, day, era)
    }

    @Throws(ArithmeticException::class)
    fun ethiopicToGregorian(year: Int, month: Int, day: Int, era: Int): IntArray {
        this.era = era
        val date = ethiopicToGregorian(year, month, day)
        unsetEra()
        return date
    }

    @Throws(ArithmeticException::class)
    fun ethiopicToGregorian(): IntArray {
        if (dateIsUnset) {
            throw ArithmeticException("Unset date.")
        }
        return ethiopicToGregorian(year, month, day)
    }

    fun ethiopicToGregorian(year: Int, month: Int, day: Int): IntArray {
        if (!isEraSet) {
            era = if (year <= 0) {
                JD_EPOCH_OFFSET_AMETE_ALEM
            } else {
                JD_EPOCH_OFFSET_AMETE_MIHRET
            }
        }
        val jdn = ethiopicToJDN(year, month, day)
        return jdnToGregorian(jdn)
    }

    @Throws(ArithmeticException::class)
    fun gregorianToEthiopic(): IntArray {
        if (dateIsUnset) {
            throw ArithmeticException("Unset date.")
        }
        return gregorianToEthiopic(year, month, day)
    }

    fun gregorianToEthiopic(year: Int, month: Int, day: Int): IntArray {
        val jdn = gregorianToJDN(year, month, day)
        return jdnToEthiopic(jdn, guessEraFromJDN(jdn))
    }

    private fun quotient(i: Long, j: Long): Int {
        return Math.floor(i.toDouble() / j).toInt()
    }

    private fun mod(i: Long, j: Long): Int {
        return (i - j * quotient(i, j)).toInt()
    }

    private fun guessEraFromJDN(jdn: Int): Int {
        return if (jdn >= JD_EPOCH_OFFSET_AMETE_MIHRET + 365) JD_EPOCH_OFFSET_AMETE_MIHRET else JD_EPOCH_OFFSET_AMETE_ALEM
    }

    private fun isGregorianLeap(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    fun jdnToGregorian(j: Int): IntArray {
        val r2000 =
            mod((j - JD_EPOCH_OFFSET_GREGORIAN).toLong(), 730485)
        val r400 =
            mod((j - JD_EPOCH_OFFSET_GREGORIAN).toLong(), 146097)
        val r100 = mod(r400.toLong(), 36524)
        val r4 = mod(r100.toLong(), 1461)
        var n = mod(r4.toLong(), 365) + 365 * quotient(r4.toLong(), 1460)
        val s = quotient(r4.toLong(), 1095)
        val aprime = (400 * quotient(
            (j - JD_EPOCH_OFFSET_GREGORIAN).toLong(),
            146097
        ) + 100 * quotient(r400.toLong(), 36524) + 4 * quotient(
            r100.toLong(),
            1461
        ) + quotient(r4.toLong(), 365) - quotient(r4.toLong(), 1460)
                - quotient(r2000.toLong(), 730484))
        val year = aprime + 1
        val t = quotient((364 + s - n).toLong(), 306)
        var month = t * (quotient(n.toLong(), 31) + 1) + (1 - t) * (quotient(
            (5 * (n - s) + 13).toLong(),
            153
        ) + 1)
        /*
		int day    = t * ( n - s - 31*month + 32 )
		           + ( 1 - t ) * ( n - s - 30*month - quotient((3*month - 2), 5) + 33 )
		;
		*/

        // int n2000 = quotient( r2000, 730484 );
        n += 1 - quotient(r2000.toLong(), 730484)
        var day = n
        if (r100 == 0 && n == 0 && r400 != 0) {
            month = 12
            day = 31
        } else {
            monthDays[2] = if (isGregorianLeap(year)) 29 else 28
            for (i in 1..nMonths) {
                if (n <= monthDays[i]) {
                    day = n
                    break
                }
                n -= monthDays[i]
            }
        }
        return intArrayOf(year, month, day)
    }

    fun gregorianToJDN(year: Int, month: Int, day: Int): Int {
        val s = (((quotient(year.toLong(), 4)
                - quotient((year - 1).toLong(), 4)
                - quotient(year.toLong(), 100))
                + quotient((year - 1).toLong(), 100)
                + quotient(year.toLong(), 400))
                - quotient((year - 1).toLong(), 400))
        val t = quotient((14 - month).toLong(), 12)
        val n = 31 * t * (month - 1) + (1 - t) * (59 + s + 30 * (month - 3) + quotient(
            (3 * month - 7).toLong(),
            5
        )) + day - 1
        return (((JD_EPOCH_OFFSET_GREGORIAN
                + 365 * (year - 1) + quotient((year - 1).toLong(), 4))
                - quotient((year - 1).toLong(), 100)) + quotient((year - 1).toLong(), 400)
                + n)
    }

    fun jdnToEthiopic(jdn: Int): IntArray {
        return if (isEraSet) jdnToEthiopic(jdn, jdOffset) else jdnToEthiopic(
            jdn,
            guessEraFromJDN(jdn)
        )
    }

    fun jdnToEthiopic(jdn: Int, era: Int): IntArray {
        val r = mod((jdn - era).toLong(), 1461).toLong()
        val n = (mod(r, 365) + 365 * quotient(r, 1460)).toLong()
        val year = (4 * quotient((jdn - era).toLong(), 1461)
                + quotient(r, 365)
                - quotient(r, 1460))
        val month = quotient(n, 30) + 1
        val day = mod(n, 30) + 1
        return intArrayOf(year, month, day)
    }

    @Throws(ArithmeticException::class)
    fun ethiopicToJDN(): Int {
        if (dateIsUnset) {
            throw ArithmeticException("Unset date.")
        }
        return ethiopicToJDN(year, month, day)
    }

    /**
     * Computes the Julian day number of the given Coptic or Ethiopic date.
     * This method assumes that the JDN epoch offset has been set. This method
     * is called by copticToGregorian and ethiopicToGregorian which will set
     * the jdn offset context.
     *
     * @param year a year in the Ethiopic calendar
     * @param month a month in the Ethiopic calendar
     * @param date a date in the Ethiopic calendar
     *
     * @return The Julian Day Number (JDN)
     */
    private fun ethCopticToJDN(
        year: Int,
        month: Int,
        day: Int,
        era: Int
    ): Int {
        return (era + 365
                + 365 * (year - 1) + quotient(year.toLong(), 4)
                + 30 * month + day) - 31
    }

    fun ethiopicToJDN(year: Int, month: Int, day: Int): Int {
        return if (isEraSet) ethCopticToJDN(year, month, day, jdOffset) else ethCopticToJDN(
            year,
            month,
            day,
            JD_EPOCH_OFFSET_AMETE_MIHRET
        )
    }

    @Throws(ArithmeticException::class)
    fun ethiopicToJDN(era: Int): Int {
        return ethiopicToJDN(year, month, day, era)
    }

    @Throws(ArithmeticException::class)
    fun ethiopicToJDN(year: Int, month: Int, day: Int, era: Int): Int {
        return ethCopticToJDN(year, month, day, era)
    }

    /*
     ** ********************************************************************************
     **  Methods for the Coptic Calendar
     ** ********************************************************************************
     */
    @Throws(ArithmeticException::class)
    fun copticToGregorian(): IntArray {
        if (dateIsUnset) {
            throw ArithmeticException("Unset date.")
        }
        return copticToGregorian(year, month, day)
    }

    fun copticToGregorian(year: Int, month: Int, day: Int): IntArray {
        era = JD_EPOCH_OFFSET_COPTIC
        val jdn = ethiopicToJDN(year, month, day)
        return jdnToGregorian(jdn)
    }

    @Throws(ArithmeticException::class)
    fun gregorianToCoptic(): IntArray {
        if (dateIsUnset) {
            throw ArithmeticException("Unset date.")
        }
        return gregorianToCoptic(year, month, day)
    }

    fun gregorianToCoptic(year: Int, month: Int, day: Int): IntArray {
        era = JD_EPOCH_OFFSET_COPTIC
        val jdn = gregorianToJDN(year, month, day)
        return jdnToEthiopic(jdn)
    }

    fun copticToJDN(year: Int, month: Int, day: Int): Int {
        return ethCopticToJDN(year, month, day, JD_EPOCH_OFFSET_COPTIC)
    }

    companion object {
        /*
     ** ********************************************************************************
     **  Era Definitions and Private Data
     ** ********************************************************************************
     */
        const val JD_EPOCH_OFFSET_AMETE_ALEM = -285019 // ዓ/ዓ
        const val JD_EPOCH_OFFSET_AMETE_MIHRET = 1723856 // ዓ/ም
        const val JD_EPOCH_OFFSET_COPTIC = 1824665
        const val JD_EPOCH_OFFSET_GREGORIAN = 1721426
        const val JD_EPOCH_OFFSET_UNSET = -1

        /*
     ** ********************************************************************************
     **  Conversion Methods To/From the Julian Day Number
     ** ********************************************************************************
     */
        private const val nMonths = 12
        private val monthDays = intArrayOf(
            0,
            31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31
        )
    }
}
