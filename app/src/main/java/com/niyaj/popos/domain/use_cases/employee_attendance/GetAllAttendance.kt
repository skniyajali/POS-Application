package com.niyaj.popos.domain.use_cases.employee_attendance

import com.niyaj.popos.domain.model.EmployeeAttendance
import com.niyaj.popos.domain.repository.AttendanceRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.util.toFormattedDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class GetAllAttendance(private val attendanceRepository: AttendanceRepository) {

    operator fun invoke(searchText: String = ""): Flow<Resource<List<EmployeeAttendance>>> {
        return channelFlow {
            attendanceRepository.getAllAttendance().collect{ result ->
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
                                    attendance.employee.employeeName.contains(searchText, true) ||
                                    attendance.employee.employeeSalary.contains(searchText, true) ||
                                    attendance.employee.employeePhone.contains(searchText, true) ||
                                    attendance.employee.employeePosition.contains(searchText, true) ||
                                    attendance.employee.employeeJoinedDate.contains(searchText, true) ||
                                    attendance.employee.employeeSalaryType.contains(searchText, true)
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