package com.niyaj.popos.di

import com.niyaj.popos.data.repository.*
import com.niyaj.popos.domain.repository.*
import com.niyaj.popos.domain.use_cases.add_on_item.*
import com.niyaj.popos.domain.use_cases.address.*
import com.niyaj.popos.domain.use_cases.app_settings.GetSetting
import com.niyaj.popos.domain.use_cases.app_settings.SettingsUseCases
import com.niyaj.popos.domain.use_cases.app_settings.UpdateSetting
import com.niyaj.popos.domain.use_cases.cart.*
import com.niyaj.popos.domain.use_cases.cart_order.*
import com.niyaj.popos.domain.use_cases.category.*
import com.niyaj.popos.domain.use_cases.charges.*
import com.niyaj.popos.domain.use_cases.common.CommonUseCases
import com.niyaj.popos.domain.use_cases.common.GetTotalPriceOfOrder
import com.niyaj.popos.domain.use_cases.customer.*
import com.niyaj.popos.domain.use_cases.data_deletion.DataDeletionUseCases
import com.niyaj.popos.domain.use_cases.data_deletion.DeleteAllRecords
import com.niyaj.popos.domain.use_cases.data_deletion.DeleteData
import com.niyaj.popos.domain.use_cases.delivery_partner.*
import com.niyaj.popos.domain.use_cases.delivery_partner.validation.ValidatePartnerEmail
import com.niyaj.popos.domain.use_cases.delivery_partner.validation.ValidatePartnerPhone
import com.niyaj.popos.domain.use_cases.employee.*
import com.niyaj.popos.domain.use_cases.employee_attendance.*
import com.niyaj.popos.domain.use_cases.employee_salary.*
import com.niyaj.popos.domain.use_cases.expenses.*
import com.niyaj.popos.domain.use_cases.expenses_category.*
import com.niyaj.popos.domain.use_cases.main_feed.*
import com.niyaj.popos.domain.use_cases.order.*
import com.niyaj.popos.domain.use_cases.product.*
import com.niyaj.popos.domain.use_cases.reports.DeletePastData
import com.niyaj.popos.domain.use_cases.reports.GenerateReport
import com.niyaj.popos.domain.use_cases.reports.GetProductWiseReport
import com.niyaj.popos.domain.use_cases.reports.GetReport
import com.niyaj.popos.domain.use_cases.reports.GetReportsBarData
import com.niyaj.popos.domain.use_cases.reports.ReportsUseCases
import com.niyaj.popos.realm.add_on_items.AddOnItemRepository
import com.niyaj.popos.realm.address.AddressRealmDao
import com.niyaj.popos.realm.app_settings.SettingsService
import com.niyaj.popos.realm.cart.CartRealmDao
import com.niyaj.popos.realm.cart_order.CartOrderRealmDao
import com.niyaj.popos.realm.category.CategoryRealmDao
import com.niyaj.popos.realm.charges.ChargesRealmDao
import com.niyaj.popos.realm.common.CommonRealmDao
import com.niyaj.popos.realm.customer.CustomerRealmDao
import com.niyaj.popos.realm.data_deletion.DataDeletionService
import com.niyaj.popos.realm.delivery_partner.PartnerRealmDao
import com.niyaj.popos.realm.employee.EmployeeRealmDao
import com.niyaj.popos.realm.employee_attendance.AttendanceService
import com.niyaj.popos.realm.employee_salary.SalaryRealmDao
import com.niyaj.popos.realm.expenses.ExpensesRealmDao
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealmDao
import com.niyaj.popos.realm.main_feed.MainFeedService
import com.niyaj.popos.realm.order.OrderRealmDao
import com.niyaj.popos.realm.product.ProductRealmDao
import com.niyaj.popos.realm.reports.ReportsRealmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.niyaj.popos.domain.use_cases.cart_order.PlaceAllOrder as PlaceAllOrder1

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    fun provideCartOrderRepository(cartOrderRealmDao: CartOrderRealmDao): CartOrderRepository {
        return CartOrderRepositoryImpl(cartOrderRealmDao)
    }

    @Provides
    fun provideCategoryRepository(categoryRealmDao: CategoryRealmDao): CategoryRepository {
        return CategoryRepositoryImpl(categoryRealmDao)
    }

    @Provides
    fun provideProductRepository(productRealmDao: ProductRealmDao): ProductRepository {
        return ProductRepositoryImpl(productRealmDao)
    }

    @Provides
    fun provideAddressRepository(addressRealmDao: AddressRealmDao): AddressRepository {
        return AddressRepositoryImpl(addressRealmDao)
    }

    @Provides
    fun provideCustomerRepository(customerRealmDao: CustomerRealmDao): CustomerRepository {
        return CustomerRepositoryImpl(customerRealmDao)
    }

    @Provides
    fun provideCartRepository(cartDao: CartRealmDao, cartOrderRepository: CartOrderRepository, productRepository: ProductRepository): CartRepository {
        return CartRepositoryImpl(cartDao, cartOrderRepository, productRepository)
    }

    @Provides
    fun provideOrderRepository(orderDao: OrderRealmDao, cartOrderRepository: CartOrderRepository, commonRepository: CommonRepository): OrderRepository {
        return OrderRepositoryImpl(orderDao, cartOrderRepository, commonRepository)
    }


    @Provides
    fun provideChargesRepository(chargesRealmDao: ChargesRealmDao): ChargesRepository {
        return ChargesRepositoryImpl(chargesRealmDao)
    }

    @Provides
    fun providePartnerRepository(partnerRealmDao: PartnerRealmDao): PartnerRepository {
        return PartnerRepositoryImpl(partnerRealmDao)
    }

    @Provides
    fun provideEmployeeRepository(employeeRealmDao: EmployeeRealmDao): EmployeeRepository {
        return EmployeeRepositoryImpl(employeeRealmDao)
    }

    @Provides
    fun provideExpensesCategoryRepository(expensesCategoryRealmDao: ExpensesCategoryRealmDao): ExpensesCategoryRepository {
        return ExpensesCategoryRepositoryImpl(expensesCategoryRealmDao)
    }

    @Provides
    fun provideExpensesRepository(expensesRealmDao: ExpensesRealmDao): ExpensesRepository {
        return ExpensesRepositoryImpl(expensesRealmDao)
    }

    @Provides
    fun provideCommonRepository(commonRealmDao: CommonRealmDao): CommonRepository {
        return CommonRepositoryImpl(commonRealmDao)
    }

    @Provides
    fun providePartnerEmailValidationUseCase(partnerUseCases: PartnerUseCases): ValidatePartnerEmail {
        return ValidatePartnerEmail(partnerUseCases)
    }

    @Provides
    fun providePartnerPhoneValidationUseCase(partnerUseCases: PartnerUseCases): ValidatePartnerPhone {
        return ValidatePartnerPhone(partnerUseCases)
    }

    @Provides
    fun provideReportsRepository(reportsRealmDao: ReportsRealmDao, productRepository: ProductRepository): ReportsRepository {
        return ReportsRepositoryImpl(reportsRealmDao, productRepository)
    }

    @Provides
    fun provideMainFeedRepository(mainFeedService: MainFeedService): MainFeedRepository {
        return MainFeedRepositoryImpl(mainFeedService)
    }

    @Provides
    fun provideSalaryRepository(salaryRealmDao: SalaryRealmDao): SalaryRepository {
        return SalaryRepositoryImpl(salaryRealmDao)
    }

    @Provides
    fun provideAttendanceRepository(attendanceService: AttendanceService): AttendanceRepository {
        return AttendanceRepositoryImpl(attendanceService)
    }

    @Provides
    fun provideDataDeletionRepository(dataDeletionService: DataDeletionService): DataDeletionRepository {
        return DataDeletionRepositoryImpl(dataDeletionService)
    }


    @Provides
    fun provideSettingsRepository(settingService: SettingsService): SettingsRepository {
        return SettingsRepositoryImpl(settingService)
    }

    @Provides
    fun provideCartOrderCases(cartOrderRepository: CartOrderRepository): CartOrderUseCases {
        return CartOrderUseCases(
            getLastCreatedOrderId = GetLastCreatedOrderId(cartOrderRepository),
            getAllCartOrders = GetAllCartOrders(cartOrderRepository),
            getCartOrder = GetCartOrder(cartOrderRepository),
            getSelectedCartOrder = GetSelectedCartOrder(cartOrderRepository),
            selectCartOrder = SelectCartOrder(cartOrderRepository),
            createCardOrder = CreateCardOrder(cartOrderRepository),
            updateCartOrder = UpdateCartOrder(cartOrderRepository),
            updateAddOnItemInCart = UpdateAddOnItemInCart(cartOrderRepository),
            deleteCartOrder = DeleteCartOrder(cartOrderRepository),
            placeOrder = PlaceOrder(cartOrderRepository),
            placeAllOrder = PlaceAllOrder1(cartOrderRepository),
            deleteCartOrders = DeleteCartOrders(cartOrderRepository),
        )
    }

    @Provides
    @Singleton
    fun provideCategoryCases(categoryRepository: CategoryRepository): CategoryUseCases {
        return CategoryUseCases(
            getAllCategories = GetAllCategories(categoryRepository),
            getCategoryById = GetCategoryById(categoryRepository),
            findCategoryByName = FindCategoryByName(categoryRepository),
            createNewCategory = CreateNewCategory(categoryRepository),
            updateCategory = UpdateCategory(categoryRepository),
            deleteCategory = DeleteCategory(categoryRepository),
        )
    }

    @Provides
    @Singleton
    fun provideProductCases(productRepository: ProductRepository): ProductUseCases {
        return ProductUseCases(
            getAllProducts = GetAllProducts(productRepository),
            getProductById = GetProductById(productRepository),
            getProductsByCategoryId = GetProductsByCategoryId(productRepository),
            createNewProduct = CreateNewProduct(productRepository),
            updateProduct = UpdateProduct(productRepository),
            deleteProduct = DeleteProduct(productRepository),
            increaseProductPrice = IncreaseProductPrice(productRepository),
            decreaseProductPrice = DecreaseProductPrice(productRepository),
            importProducts = ImportProducts(productRepository),
            findProductByName = FindProductByName(productRepository),

        )
    }

    @Provides
    @Singleton
    fun provideAddressCases(addressRepository: AddressRepository): AddressUseCases {
        return AddressUseCases(
            getAllAddress = GetAllAddress(addressRepository),
            getAddressById = GetAddressById(addressRepository),
            createNewAddress = CreateNewAddress(addressRepository),
            updateAddress = UpdateAddress(addressRepository),
            deleteAddress = DeleteAddress(addressRepository),
        )
    }

    @Provides
    @Singleton
    fun provideCustomerCases(customerRepository: CustomerRepository): CustomerUseCases {
        return CustomerUseCases(
            getAllCustomers = GetAllCustomers(customerRepository),
            getCustomerById = GetCustomerById(customerRepository),
            findCustomerByPhone = FindCustomerByPhone(customerRepository),
            createNewCustomer = CreateNewCustomer(customerRepository),
            updateCustomer = UpdateCustomer(customerRepository),
            deleteCustomer = DeleteCustomer(customerRepository),
        )
    }

    @Provides
    @Singleton
    fun provideCartCases(cartRepository: CartRepository): CartUseCases {
        return CartUseCases(
            getAllDineInOrders = GetAllDineInOrders(cartRepository),
            getAllDineOutOrders = GetAllDineOutOrders(cartRepository),
            getAllCartItems = GetAllCartItems(cartRepository),
            getSelectedCartItems = GetSelectedCartItems(cartRepository),
            selectCartItem = SelectCartItem(cartRepository),
            selectAllCartItem = SelectAllCartItem(cartRepository),
            addProductToCart = AddProductToCart(cartRepository),
            removeProductFromCart = RemoveProductFromCart(cartRepository),
            deleteCartItem = DeleteCartItem(cartRepository),
            getMainFeedProductQuantity = GetMainFeedProductQuantity(cartRepository),
        )
    }

    @Provides
    @Singleton
    fun provideOrderCases(orderRepository: OrderRepository): OrderUseCases {
        return OrderUseCases(
            getAllOrders = GetAllOrders(orderRepository),
            changeOrderStatus = ChangeOrderStatus(orderRepository),
            getOrderDetails = GetOrderDetails(orderRepository),
            deleteOrder = DeleteOrder(orderRepository),
        )
    }

    @Singleton
    @Provides
    fun provideMainFeedUseCases(mainFeedRepository: MainFeedRepository, cartOrderRepository: CartOrderRepository): MainFeedUseCases{
        return MainFeedUseCases(
            getMainFeedProducts = GetMainFeedProducts(mainFeedRepository),
            getMainFeedSelectedOrder = GetMainFeedSelectedOrder(mainFeedRepository, cartOrderRepository),
            getMainFeedCategories = GetMainFeedCategories(mainFeedRepository),
            getProductsPager = GetProductsPager(mainFeedRepository),
        )
    }

    @Provides
    @Singleton
    fun provideAddOnItemUseCases(addOnItemRepository: AddOnItemRepository): AddOnItemUseCases {
        return AddOnItemUseCases(
            getAllAddOnItems = GetAllAddOnItems(addOnItemRepository),
            getAddOnItemById = GetAddOnItemById(addOnItemRepository),
            findAddOnItemByName = FindAddOnItemByName(addOnItemRepository),
            createNewAddOnItem = CreateNewAddOnItem(addOnItemRepository),
            updateAddOnItem = UpdateAddOnItem(addOnItemRepository),
            deleteAddOnItem = DeleteAddOnItem(addOnItemRepository),
        )
    }

    @Provides
    @Singleton
    fun provideChargesUseCases(chargesRepository: ChargesRepository): ChargesUseCases {
        return ChargesUseCases(
            getAllCharges = GetAllCharges(chargesRepository),
            getChargesById = GetChargesById(chargesRepository),
            findChargesByName = FindChargesByName(chargesRepository),
            createNewCharges = CreateNewCharges(chargesRepository),
            updateCharges = UpdateCharges(chargesRepository),
            deleteCharges = DeleteCharges(chargesRepository),
        )
    }

    @Provides
    @Singleton
    fun providePartnerUseCases(partnerRepository: PartnerRepository): PartnerUseCases {
        return PartnerUseCases(
            getAllPartners = GetAllPartners(partnerRepository),
            getPartnerById = GetPartnerById(partnerRepository),
            createNewPartner = CreateNewPartner(partnerRepository),
            updatePartner = UpdatePartner(partnerRepository),
            deletePartner = DeletePartner(partnerRepository),
            getPartnerByEmail = GetPartnerByEmail(partnerRepository),
            getPartnerByPhone = GetPartnerByPhone(partnerRepository)
        )
    }

    @Provides
    @Singleton
    fun provideEmployeeUseCases(employeeRepository: EmployeeRepository): EmployeeUseCases {
        return EmployeeUseCases(
            getAllEmployee = GetAllEmployee(employeeRepository),
            getEmployeeById = GetEmployeeById(employeeRepository),
            findEmployeeByName = FindEmployeeByName(employeeRepository),
            findEmployeeByPhone = FindEmployeeByPhone(employeeRepository),
            createNewEmployee = CreateNewEmployee(employeeRepository),
            updateEmployee = UpdateEmployee(employeeRepository),
            deleteEmployee = DeleteEmployee(employeeRepository),
        )
    }

    @Provides
    @Singleton
    fun provideExpensesCategoryUseCases(expensesCategoryRepository: ExpensesCategoryRepository): ExpensesCategoryUseCases {
        return ExpensesCategoryUseCases(
            getAllExpensesCategory = GetAllExpensesCategory(expensesCategoryRepository),
            getExpensesCategoryById = GetExpensesCategoryById(expensesCategoryRepository),
            createNewExpensesCategory = CreateNewExpensesCategory(expensesCategoryRepository),
            updateExpensesCategory = UpdateExpensesCategory(expensesCategoryRepository),
            deleteExpensesCategory = DeleteExpensesCategory(expensesCategoryRepository)
        )
    }

    @Provides
    @Singleton
    fun provideExpensesUseCases(expensesRepository: ExpensesRepository): ExpensesUseCases {
        return ExpensesUseCases(
            getAllExpenses = GetAllExpenses(expensesRepository),
            getExpensesById = GetExpensesById(expensesRepository),
            createNewExpenses = CreateNewExpenses(expensesRepository),
            updateExpenses = UpdateExpenses(expensesRepository),
            deleteExpenses = DeleteExpenses(expensesRepository),
            deletePastExpenses = DeletePastExpenses(expensesRepository),
        )
    }

    @Provides
    @Singleton
    fun provideCommonUseCases(commonRepository: CommonRepository): CommonUseCases {
        return CommonUseCases(
            getTotalPriceOfOrder = GetTotalPriceOfOrder(commonRepository)
        )
    }

    @Provides
    @Singleton
    fun provideReportsUseCases(reportsRepository: ReportsRepository): ReportsUseCases {
        return ReportsUseCases(
            generateReport = GenerateReport(reportsRepository),
            getReport = GetReport(reportsRepository),
            getReportsBarData = GetReportsBarData(reportsRepository),
            getProductWiseReport = GetProductWiseReport(reportsRepository),
            deletePastData = DeletePastData(reportsRepository),
        )
    }

    @Provides
    @Singleton
    fun provideSalaryUseCases(salaryRepository: SalaryRepository): SalaryUseCases {
        return SalaryUseCases(
            getAllSalary = GetAllSalary(salaryRepository),
            getSalaryById = GetSalaryById(salaryRepository),
            getSalaryByEmployeeId = GetSalaryByEmployeeId(salaryRepository),
            addNewSalary = AddNewSalary(salaryRepository),
            updateSalary = UpdateSalary(salaryRepository),
            deleteSalary = DeleteSalary(salaryRepository),
            getEmployeeSalary = GetEmployeeSalary(salaryRepository),
            getSalaryCalculableDate = GetSalaryCalculableDate(salaryRepository),
        )
    }

    @Provides
    @Singleton
    fun provideAttendanceUseCases(attendanceRepository: AttendanceRepository): AttendanceUseCases {
        return AttendanceUseCases(
            getAllAttendance = GetAllAttendance(attendanceRepository),
            getAttendanceById = GetAttendanceById(attendanceRepository),
            findAttendanceByAbsentDate = FindAttendanceByAbsentDate(attendanceRepository),
            addAbsentEntry = AddAbsentEntry(attendanceRepository),
            updateAbsentEntry = UpdateAbsentEntry(attendanceRepository),
            deleteAttendanceById = DeleteAttendanceById(attendanceRepository),
            deleteAttendanceByEmployeeId = DeleteAttendanceByEmployeeId(attendanceRepository),
            getMonthlyAbsentReports = GetMonthlyAbsentReports(attendanceRepository),

        )
    }

    @Provides
    @Singleton
    fun provideDataDeletionUseCases(dataDeletionRepository: DataDeletionRepository): DataDeletionUseCases {
        return DataDeletionUseCases(
            deleteData = DeleteData(dataDeletionRepository),
            deleteAllRecords = DeleteAllRecords(dataDeletionRepository),
        )
    }

    @Provides
    @Singleton
    fun provideSettingsUseCases(settingsRepository: SettingsRepository): SettingsUseCases {
        return SettingsUseCases(
            getSetting = GetSetting(settingsRepository),
            updateSetting = UpdateSetting(settingsRepository)
        )
    }
}
