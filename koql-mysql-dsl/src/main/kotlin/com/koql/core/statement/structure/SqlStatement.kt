package com.koql.core.statement.structure


import com.koql.core.dsl.mysql.config.Configuration
import com.koql.core.statement.common.StatementPart


abstract class SqlStatement(val config : Configuration) : StatementPart


