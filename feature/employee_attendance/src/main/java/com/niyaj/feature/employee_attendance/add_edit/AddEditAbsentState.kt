package com.niyaj.feature.employee_attendance.add_edit

import com.niyaj.common.utils.toMilliSecond
import java.time.LocalDate

data class AddEditAbsentState(
    val absentDate: String = LocalDate.now().toMilliSecond,

    val absentReason: String = "",
)
