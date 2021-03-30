package com.koql.core.dsl.mysql.config

import com.koql.core.dsl.mysql.structure.AliasTable
import com.koql.core.dsl.mysql.structure.MysqlTable

data class RenderConfig(
    var hasAliasTable : Boolean = false,
    val aliasTableMap: MutableMap<MysqlTable , AliasTable> = mutableMapOf()
)
