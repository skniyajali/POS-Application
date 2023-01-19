package com.niyaj.popos.features.employee.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.niyaj.popos.di.TestConfig
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.util.EmployeeSalaryType
import com.niyaj.popos.features.employee.domain.util.EmployeeType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@OptIn(ExperimentalCoroutinesApi::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class EmployeeRepositoryImplTest {

    private lateinit var repository: EmployeeRepositoryImpl

    private val newEmployee = Employee(
        employeeId = "1111",
        employeeName = "New employee",
        employeePhone = "9078563412",
        employeeSalary = "10000",
        employeeSalaryType = EmployeeSalaryType.Weekly.salaryType,
        employeePosition = "Master",
        employeeType = EmployeeType.FullTime.employeeType,
        employeeJoinedDate = System.currentTimeMillis().toString(),
        createdAt = System.currentTimeMillis().toString(),
    )

    private val updatedEmployee = Employee(
        employeeId = "1111",
        employeeName = "Updated employee",
        employeePhone = "7078563443",
        employeeSalary = "12000",
        employeeSalaryType = EmployeeSalaryType.Monthly.salaryType,
        employeePosition = "Chef",
        employeeType = EmployeeType.PartTime.employeeType,
        employeeJoinedDate = System.currentTimeMillis().toString(),
        updatedAt = System.currentTimeMillis().toString(),
    )

    @Before
    fun setUp() = runTest {
        val config = TestConfig.config()
        val dispatcher = TestConfig.testDispatcher(testScheduler)

        repository = EmployeeRepositoryImpl(config, dispatcher)
    }

    @After
    fun tearDown() {
        TestConfig.clearDatabase()
    }


    @Test
    fun a_create_new_employee_with_invalid_data_return_false() = runTest {
        val result = repository.createNewEmployee(Employee())

        assertThat(result.data).isNotNull()
        assertThat(result.data).isFalse()
        assertThat(result.message).isNotNull()
        assertThat(result.message).isEqualTo("Unable to validate employee")
    }

    @Test
    fun b_create_new_employee_with_valid_data_return_true() = runTest {
        val result = repository.createNewEmployee(newEmployee)

        assertThat(result.data).isNotNull()
        assertThat(result.data).isTrue()
        assertThat(result.message).isNull()
    }

    @Test
    fun c_get_employee_with_invalid_id_return_null() = runTest {
        val result = repository.getEmployeeById("90ss")

        assertThat(result.data).isNull()
    }

    @Test
    fun d_get_employee_with_valid_id_return_employee() = runTest {
        val result = repository.getEmployeeById(newEmployee.employeeId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.employeeId).isEqualTo(newEmployee.employeeId)
        assertThat(result.data?.employeeName).isEqualTo(newEmployee.employeeName)
        assertThat(result.data?.employeeSalary).isEqualTo(newEmployee.employeeSalary)
        assertThat(result.data?.employeePhone).isEqualTo(newEmployee.employeePhone)
    }

    @Test
    fun e_validate_employee_name_with_empty_data_return_false() = runTest {
        val result = repository.validateEmployeeName("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Employee name must not be empty")
    }

    @Test
    fun f_validate_employee_name_with_invalid_data_return_false() = runTest {
        val result = repository.validateEmployeeName("nd")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Employee name must be more than 4 characters")
    }

    @Test
    fun g_validate_employee_name_with_invalid_data_amd_digit_return_false() = runTest {
        val result = repository.validateEmployeeName("ndee2")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Employee name must not contain any digit")
    }

    @Test
    fun h_validate_employee_name_that_already_exist_return_false() = runTest {
        val result = repository.validateEmployeeName(newEmployee.employeeName)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Employee name already exists.")
    }

    @Test
    fun i_validate_employee_name_that_already_exist_with_id_return_true() = runTest {
        val result = repository.validateEmployeeName(newEmployee.employeeName, newEmployee.employeeId)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun j_validate_employee_name_with_valid_data_return_true() = runTest {
        val result = repository.validateEmployeeName("customer")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun k_validate_employee_position_with_empty_data_return_false() = runTest {
        val result = repository.validateEmployeePosition("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Employee position is required")
    }

    @Test
    fun l_validate_employee_position_with_valid_data_return_true() = runTest {
        val result = repository.validateEmployeePosition(newEmployee.employeePosition)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun m_validate_employee_salary_with_empty_data_return_false() = runTest {
        val result = repository.validateEmployeeSalary("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Salary must not be empty")
    }

    @Test
    fun n_validate_employee_salary_with_invalid_data_return_false() = runTest {
        val result = repository.validateEmployeeSalary("100000")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Salary is in invalid")
    }

    @Test
    fun o_validate_employee_salary_with_invalid_data_with_letter_return_false() = runTest {
        val result = repository.validateEmployeeSalary("sd100")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Salary must not contain any characters")
    }

    @Test
    fun p_validate_employee_salary_with_valid_data_return_true() = runTest {
        val result = repository.validateEmployeeSalary(newEmployee.employeeSalary)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun q_validate_employee_phone_with_empty_data_return_false() = runTest {
        val result = repository.validateEmployeePhone("")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone no must not be empty")
    }

    @Test
    fun r_validate_employee_phone_with_less_data_return_false() = runTest {
        val result = repository.validateEmployeePhone("78983")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
    }

    @Test
    fun s_validate_employee_phone_with_data_and_digit_return_false() = runTest {
        val result = repository.validateEmployeePhone("789838933c")

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone must not contain a letter")
    }

    @Test
    fun t_validate_employee_phone_with_data_that_already_exist_return_false() = runTest {
        val result = repository.validateEmployeePhone(newEmployee.employeePhone)

        assertThat(result.successful).isFalse()
        assertThat(result.errorMessage).isNotNull()
        assertThat(result.errorMessage).isEqualTo("Phone no already exists")
    }

    @Test
    fun u_validate_employee_phone_with_data_that_already_exist_with_id_return_true() = runTest {
        val result = repository.validateEmployeePhone(newEmployee.employeePhone, newEmployee.employeeId)

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun v_validate_employee_phone_with_valid_data_return_true() = runTest {
        val result = repository.validateEmployeePhone("7856341290")

        assertThat(result.successful).isTrue()
        assertThat(result.errorMessage).isNull()
    }

    @Test
    fun w_update_employee_with_invalid_data_return_false() = runTest {
        val repository = repository.updateEmployee(Employee(), "")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to validate employee")
    }

    @Test
    fun x_update_employee_with_valid_data_and_invalid_id_return_false() = runTest {
        val repository = repository.updateEmployee(updatedEmployee, "89dhjd")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to find employee")
    }

    @Test
    fun y_update_employee_with_valid_data_return_true() = runTest {
        val resource = repository.updateEmployee(updatedEmployee, newEmployee.employeeId)

        assertThat(resource.data).isNotNull()
        assertThat(resource.data).isTrue()
        assertThat(resource.message).isNull()

        val result = repository.getEmployeeById(updatedEmployee.employeeId)

        assertThat(result.data).isNotNull()
        assertThat(result.message).isNull()
        assertThat(result.data?.employeeId).isEqualTo(updatedEmployee.employeeId)
        assertThat(result.data?.employeeName).isEqualTo(updatedEmployee.employeeName)
        assertThat(result.data?.employeeSalary).isEqualTo(updatedEmployee.employeeSalary)
        assertThat(result.data?.employeePhone).isEqualTo(updatedEmployee.employeePhone)
    }

    @Test
    fun z0_find_employee_by_phone_return_true() = runTest {
        val result = repository.findEmployeeByPhone(updatedEmployee.employeePhone, null)

        assertThat(result).isTrue()
    }

    @Test
    fun z1_find_employee_by_phone_with_id_return_false() = runTest {
        val result = repository.findEmployeeByPhone(updatedEmployee.employeePhone, updatedEmployee.employeeId)

        assertThat(result).isFalse()
    }

    @Test
    fun z2_find_employee_by_name_return_true() = runTest {
        val result = repository.findEmployeeByName(updatedEmployee.employeeName, null)

        assertThat(result).isTrue()
    }

    @Test
    fun z3_find_employee_by_name_with_id_return_true() = runTest {
        val result = repository.findEmployeeByName(updatedEmployee.employeeName, updatedEmployee.employeeId)

        assertThat(result).isFalse()
    }

    @Test
    fun z4_delete_employee_with_invalid_id_return_false() = runTest {
        val repository = repository.deleteEmployee("09sd")

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isFalse()
        assertThat(repository.message).isNotNull()
        assertThat(repository.message).isEqualTo("Unable to find employee")
    }

    @Test
    fun z5_delete_employee_with_valid_id_return_true() = runTest {
        val repository = repository.deleteEmployee(updatedEmployee.employeeId)

        assertThat(repository.data).isNotNull()
        assertThat(repository.data).isTrue()
        assertThat(repository.message).isNull()
    }

    @Test
    fun z6_get_all_employees_return_employees() = runTest {
        val data = createNewEmployees()
        assertThat(data).isTrue()

        repository.getAllEmployee().onEach {result ->
            when (result) {
                is Resource.Success -> {
                    assertThat(result.data).isNotNull()
                    assertThat(result.message).isNull()
                    assertThat(result.data?.size).isEqualTo(5)
                }
                else -> {}
            }
        }

    }

    private fun createNewEmployees(): Boolean {
        return try {
            val employees = mutableListOf<Employee>()

            ('A'..'E').filterIndexed { index, c ->
                employees.add(
                    Employee(
                        employeeId = index.plus(1111).toString(),
                        employeeName = c.plus("New employee"),
                        employeePhone = index.plus(9078560000).toString(),
                        employeeSalary = index.plus(10000).toString(),
                        employeeSalaryType = EmployeeSalaryType.Weekly.salaryType,
                        employeePosition = "Master",
                        employeeType = EmployeeType.FullTime.employeeType,
                        employeeJoinedDate = System.currentTimeMillis().toString(),
                        createdAt = System.currentTimeMillis().toString(),
                    )
                )
            }

            runTest {
                employees.forEach { employee ->
                    val result = repository.createNewEmployee(employee)

                    assertThat(result.data).isNotNull()
                    assertThat(result.data).isTrue()
                    assertThat(result.message).isNull()
                }
            }

            true
        }catch (e: AssertionError) {
            false
        }
    }

}