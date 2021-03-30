package com.koql.core.condition

import com.koql.core.config.Configuration
import com.koql.core.statement.structure.Column
import com.koql.core.statement.structure.Field
import com.koql.core.statement.structure.ValueField

open class CompareCondition<T>(
    val field: Field<T>,
    val valueField: Field<T>,
    val op: Comparator

) : Condition() {

    constructor(
        field: Field<T>,
        value: T,
        op: Comparator
    ) : this(
        field,
        ValueField(
            if (field is Column) {
               field.schema.name + "."+ field.table.name + "."+ field.name
            } else {
                ValueField.randomKey()
            },
            value
        ),
        op
    )


    override fun render(config: Configuration): String {
        val sql = "${field.render(config)} ${op.value} ${valueField.render(config)}"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = field.parameters().plus(valueField.parameters()) as MutableMap<String, Any?>
        return params
    }
}



open class LikeCondition(
    val field: Field<*>,
    val valueField: ValueField<String>,
    val op: LikeComparator

) : Condition() {

    constructor(
        field: Field<*>,
        value: String,
        op: LikeComparator
    ) : this(
        field,
        ValueField(
            if (field is Column) {
                field.schema.name + "."+ field.table.name + "."+ field.name
            } else {
                ValueField.randomKey()
            },
            value
        ),
        op
    )


    override fun render(config: Configuration): String {
        val sql = "${field.render(config)} ${op.value} ${valueField.render(config)}"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = field.parameters().plus(valueField.parameters()) as MutableMap<String, Any?>
        return params
    }
}

open class BetweenConditionPart<T>(
    val prefix: Field<T> ,
    val field: Field<T>
){

    constructor(
        field: Field<T>,
        value: T
    ) : this(
        field,
        ValueField(
            if (field is Column) {
                field.schema.name + "."+ field.table.name + "."+ field.name  + "-between"
            } else {
                ValueField.randomKey()
            },
            value
        )
    )


    fun and(value: T) : BetweenAndCondition<T> {
       return BetweenAndCondition(this , value)
    }

    fun and(value: Field<T>) : BetweenAndCondition<T> {
        return BetweenAndCondition(this , value)
    }

}


open class BetweenAndCondition<T>(
    val prefix: Field<T> ,
    val between: Field<T>,
    val and: Field<T>
) : Condition() {

    constructor(
         prefix: Field<T> ,
         betweenValue: T,
        andValue: T,

        ) : this(
        prefix,
        ValueField(
            if (prefix is Column) {
                prefix.schema.name + "."+ prefix.table.name + "."+ prefix.name  + "-between"
            } else {
                ValueField.randomKey()
            },
            betweenValue
        ) ,

        ValueField(
            if (prefix is Column) {
                prefix.schema.name + "."+ prefix.table.name + "."+ prefix.name + "-and"
            } else {
                ValueField.randomKey()
            },
            andValue
        )
    )

    constructor(
        prefix: Field<T> ,
        betweenValue: T,
        and: Field<T>,

        ) : this(
        prefix,
        ValueField(
            if (prefix is Column) {
                prefix.schema.name + "."+ prefix.table.name + "."+ prefix.name  + "-between"
            } else {
                ValueField.randomKey()
            },
            betweenValue
        ) ,

        and
    )
    constructor(
        prefix: Field<T> ,
        between: Field<T>,
        andValue: T,

        ) : this(
        prefix,
        between,

        ValueField(
            if (prefix is Column) {
                prefix.schema.name + "."+ prefix.table.name + "."+ prefix.name + "-and"
            } else {
                ValueField.randomKey()
            },
            andValue
        )
    )

    constructor(
        betweenPart: BetweenConditionPart<T>,
        and: Field<T>,

    ) : this(
        betweenPart.prefix,
        betweenPart.field ,
        and
    )

    constructor(
        betweenPart: BetweenConditionPart<T>,
        andValue: T,

        ) : this(
        betweenPart.prefix,
        betweenPart.field ,
        ValueField(
            if (betweenPart.prefix is Column) {
                betweenPart.prefix.schema.name + "."+ betweenPart.prefix.table.name + "."+ betweenPart.prefix.name + "-and"
            } else {
                ValueField.randomKey()
            },
            andValue
        )
    )

    companion object {
        @JvmStatic
        val BETWEEN = "BETWEEN"
        @JvmStatic
        val AND = "AND"
    }

    override fun render(config: Configuration): String {
        val sql = "${prefix.render(config)} $BETWEEN ${between.render(config)} $AND ${and.render(config)}"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {
        val params = (prefix.parameters() + between.parameters() + and.parameters()) as MutableMap<String, Any?>
        return params
    }
}


open class InCondition<T>(
    val prefix: Field<T> ,
    val inList : List<Field<T>> ,
    val op : InComparator
) : Condition() {


    constructor(
        prefix: Field<T> ,
        inList: Array<out T>,
        op : InComparator,

        ) : this(
        prefix,
        inList.mapIndexed { i: Int, t: T ->
            ValueField(
                       if (prefix is Column) {
                           prefix.schema.name + "."+ prefix.table.name + "."+ prefix.name + "-$i"
                       } else {
                           ValueField.randomKey()
                       },
                t
                   )
        },

        op
    )


    override fun render(config: Configuration): String {
        val sql = "${prefix.render(config)} ${op.value} ( ${inList.joinToString(",") { it.render(config) }} )"
        return sql
    }

    override fun parameters(): MutableMap<String, Any?> {


        val params = ( prefix.parameters() + inList.map {
            it.parameters()
        }
            .flatMap {
            it.toList()
        }
            .toMap() )as MutableMap<String, Any?>

        return params
    }
}

