package com.niyaj.popos.features.employee_attendance.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.util.toFormattedDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class GetAllAttendance(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(searchText: String = ""): Flow<Resource<List<EmployeeAttendance>>> {
        return channelFlow {
            withContext(Dispatchers.IO){
                attendanceRepository.getAllAttendance().collectLatest{ result ->
                    when (result){
                        is Resource.Loading -> {
                            send(Resource.Loading(result.isLoading))
                        }
                        is Resource.Success -> {
                            val data = result.data?.let {
                                it.filter { attendance ->
                                    if (searchText.isNotEmpty()) {
                                        attendance.absentDate.toFormattedDate.contains(searchText, true) ||
                                                attendance.absentReason.contains(searchText, true) ||
                                                attendance.isAbsent.toString().contains(searchText, true) ||
                                                attendance.employee?.employeeName?.contains(searchText, true) == true ||
                                                attendance.employee?.employeeSalary?.contains(searchText, true) == true ||
                                                attendance.employee?.employeePhone?.contains(searchText, true) == true ||
                                                attendance.employee?.employeePosition?.contains(searchText, true) == true ||
                                                attendance.employee?.employeeJoinedDate?.contains(searchText, true) == true ||
                                                attendance.employee?.employeeSalaryType?.contains(searchText, true) == true
                                    }else true
                                }
                            }
                            send(Resource.Success(data))
                        }
                        is Resource.Error -> {
                            send(Resource.Error(result.message ?: "Unable to get data from repository"))
                        }
                    }
                }
            }
        }
    }
}