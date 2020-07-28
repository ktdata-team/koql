package com.koql.core.statement.structure


abstract class ColumnSet  {

    /** Return the column set that contains this field set. */
    val source: ColumnSet = this
    abstract val columns: List<Column<*>>
    /** Returns the field of this field set. */
    val fields: List<Expression<*>> = columns

    /**
     * Returns all real fields, unrolling composite [CompositeColumn] if present
     */
    val realFields: List<Expression<*>>
        get() {
            val unrolled = ArrayList<Expression<*>>(fields.size)

            fields.forEach {
                 unrolled.add(it)
            }

            return unrolled
        }



}

open class Table(name: String = "") : ColumnSet() {
    /** Returns the table name. */
    open val tableName: String = if (name.isNotEmpty()) name else this.javaClass.simpleName.removeSuffix("Table")
    internal val tableNameWithoutScheme: String get() = tableName.substringAfter(".")

    private val _columns = mutableListOf<Column<*>>()
    /** Returns all the columns defined on the table. */
    override val columns: List<Column<*>> get() = _columns



    // Column registration

    /** Adds a column of the specified [type] and with the specified [name] to the table. */
    fun <T> registerColumn(name: String): Column<T> = Column<T>(this, name).also { _columns.addColumn(it) }

    /**
     * Replaces the specified [oldColumn] with the specified [newColumn] in the table.
     * Mostly used internally by the library.
     */
    fun <TColumn : Column<*>> replaceColumn(oldColumn: Column<*>, newColumn: TColumn): TColumn {
        _columns.remove(oldColumn)
        _columns.addColumn(newColumn)
        return newColumn
    }

    private fun MutableList<Column<*>>.addColumn(column: Column<*>) {
        if (this.any { it.name == column.name }) {
            error(column.name +"  " + tableName)
        }
        this.add(column)
    }


    // Numeric columns

  /*  *//** Creates a numeric column, with the specified [name], for storing 1-byte integers. *//*
    fun byte(name: String): Column<Byte> = registerColumn(name, ByteColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 1-byte unsigned integers. *//*
    @ExperimentalUnsignedTypes
    fun ubyte(name: String): Column<UByte> = registerColumn(name, UByteColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 2-byte integers. *//*
    fun short(name: String): Column<Short> = registerColumn(name, ShortColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 2-byte unsigned integers. *//*
    @ExperimentalUnsignedTypes
    fun ushort(name: String): Column<UShort> = registerColumn(name, UShortColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 4-byte integers. *//*
    fun integer(name: String): Column<Int> = registerColumn(name, IntegerColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 4-byte unsigned integers. *//*
    @ExperimentalUnsignedTypes
    fun uinteger(name: String): Column<UInt> = registerColumn(name, UIntegerColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 8-byte integers. *//*
    fun long(name: String): Column<Long> = registerColumn(name, LongColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 8-byte unsigned integers. *//*
    @ExperimentalUnsignedTypes
    fun ulong(name: String): Column<ULong> = registerColumn(name, ULongColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 4-byte (single precision) floating-point numbers. *//*
    fun float(name: String): Column<Float> = registerColumn(name, FloatColumnType())

    *//** Creates a numeric column, with the specified [name], for storing 8-byte (double precision) floating-point numbers. *//*
    fun double(name: String): Column<Double> = registerColumn(name, DoubleColumnType())

    *//**
     * Creates a numeric column, with the specified [name], for storing numbers with the specified [precision] and [scale].
     *
     * To store the decimal `123.45`, [precision] would have to be set to 5 (as there are five digits in total) and
     * [scale] to 2 (as there are two digits behind the decimal point).
     *
     * @param name Name of the column.
     * @param precision Total count of significant digits in the whole number, that is, the number of digits to both sides of the decimal point.
     * @param scale Count of decimal digits in the fractional part.
     *//*
    fun decimal(name: String, precision: Int, scale: Int): Column<BigDecimal> = registerColumn(name, DecimalColumnType(precision, scale))

    // Character columns

    *//** Creates a character column, with the specified [name], for storing single characters. *//*
    fun char(name: String): Column<Char> = registerColumn(name, CharacterColumnType())

    *//**
     * Creates a character column, with the specified [name], for storing strings with the specified [length] using the specified text [collate] type.
     * If no collate type is specified then the database default is used.
     *//*
    fun char(name: String, length: Int, collate: String? = null): Column<String> = registerColumn(name, CharColumnType(length, collate))

    *//**
     * Creates a character column, with the specified [name], for storing strings with the specified maximum [length] using the specified text [collate] type.
     * If no collate type is specified then the database default is used.
     *//*
    fun varchar(name: String, length: Int, collate: String? = null): Column<String> = registerColumn(name, VarCharColumnType(length, collate))

    *//**
     * Creates a character column, with the specified [name], for storing strings of arbitrary length using the specified [collate] type.
     * If no collate type is specified then the database default is used.
     *
     * Some database drivers do not load text content immediately (by performance and memory reasons)
     * what means that you can obtain column value only within the open transaction.
     * If you desire to make content available outside the transaction use [eagerLoading] param.
     *//*
    fun text(name: String, collate: String? = null, eagerLoading: Boolean = false): Column<String> = registerColumn(name, TextColumnType(collate, eagerLoading))

    // Binary columns

    *//**
     * Creates a binary column, with the specified [name], for storing byte arrays of arbitrary size.
     *
     * **Note:** This function is only supported by Oracle and PostgeSQL dialects, for the rest please specify a length.
     *
     * @sample org.jetbrains.exposed.sql.tests.shared.DDLTests.testBinaryWithoutLength
     *//*
    fun binary(name: String): Column<ByteArray> = registerColumn(name, BasicBinaryColumnType())

    *//**
     * Creates a binary column, with the specified [name], for storing byte arrays with the specified maximum [length].
     *
     * @sample org.jetbrains.exposed.sql.tests.shared.DDLTests.testBinary
     *//*
    fun binary(name: String, length: Int): Column<ByteArray> = registerColumn(name, BinaryColumnType(length))

    *//**
     * Creates a binary column, with the specified [name], for storing BLOBs.
     *
     * @sample org.jetbrains.exposed.sql.tests.shared.DDLTests.testBlob
     *//*
    fun blob(name: String): Column<ExposedBlob> = registerColumn(name, BlobColumnType())

    *//** Creates a binary column, with the specified [name], for storing UUIDs. *//*
    fun uuid(name: String): Column<UUID> = registerColumn(name, UUIDColumnType())

    // Boolean columns

    *//** Creates a column, with the specified [name], for storing boolean values. *//*
    fun bool(name: String): Column<Boolean> = registerColumn(name, BooleanColumnType())

    // Enumeration columns

    *//** Creates an enumeration column, with the specified [name], for storing enums of type [klass] by their ordinal. *//*
    fun <T : Enum<T>> enumeration(name: String, klass: KClass<T>): Column<T> = registerColumn(name, EnumerationColumnType(klass))

    *//**
     * Creates an enumeration column, with the specified [name], for storing enums of type [klass] by their name.
     * With the specified maximum [length] for each name value.
     *//*
    fun <T : Enum<T>> enumerationByName(name: String, length: Int, klass: KClass<T>): Column<T> = registerColumn(name, EnumerationNameColumnType(klass, length))

    *//**
     * Creates an enumeration column with custom SQL type.
     * The main usage is to use a database specific type.
     *
     * See [https://github.com/JetBrains/Exposed/wiki/DataTypes#how-to-use-database-enum-types] for more details.
     *
     * @param name The column name
     * @param sql A SQL definition for the column
     * @param fromDb A lambda to convert a value received from a database to an enumeration instance
     * @param toDb A lambda to convert an enumeration instance to a value which will be stored to a database
     *//*
    @Suppress("UNCHECKED_CAST")
    fun <T : Enum<T>> customEnumeration(
        name: String,
        sql: String? = null,
        fromDb: (Any) -> T,
        toDb: (T) -> Any
    ): Column<T> = registerColumn(name, object : StringColumnType() {
        override fun sqlType(): String = sql ?: error("Column $name should exists in database ")
        override fun valueFromDB(value: Any): T = if (value::class.isSubclassOf(Enum::class)) value as T else fromDb(value)
        override fun notNullValueToDB(value: Any): Any = toDb(value as T)
    })*/



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Table) return false

        if (tableName != other.tableName) return false

        return true
    }

    override fun hashCode(): Int = tableName.hashCode()
}