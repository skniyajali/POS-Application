package com.niyaj.popos.di

import com.niyaj.popos.data.repository.*
import com.niyaj.popos.domain.repository.*
import com.niyaj.popos.domain.use_cases.cart.*
import com.niyaj.popos.domain.use_cases.cart_order.*
import com.niyaj.popos.domain.use_cases.common.CommonUseCases
import com.niyaj.popos.domain.use_cases.common.GetTotalPriceOfOrder
import com.niyaj.popos.domain.use_cases.delivery_partner.*
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
import com.niyaj.popos.realm.cart.CartRealmDao
import com.niyaj.popos.realm.cart_order.CartOrderRealmDao
import com.niyaj.popos.realm.common.CommonRealmDao
import com.niyaj.popos.realm.employee_salary.data.repository.SalaryRepositoryImpl
import com.niyaj.popos.realm.employee_salary.domain.use_cases.AddNewSalary
import com.niyaj.popos.realm.employee_salary.domain.use_cases.DeleteSalary
import com.niyaj.popos.realm.employee_salary.domain.use_cases.GetAllSalary
import com.niyaj.popos.realm.employee_salary.domain.use_cases.GetEmployeeSalary
import com.niyaj.popos.realm.employee_salary.domain.use_cases.GetSalaryByEmployeeId
import com.niyaj.popos.realm.employee_salary.domain.use_cases.GetSalaryById
import com.niyaj.popos.realm.employee_salary.domain.use_cases.GetSalaryCalculableDate
import com.niyaj.popos.realm.employee_salary.domain.use_cases.SalaryUseCases
import com.niyaj.popos.realm.employee_salary.domain.use_cases.UpdateSalary
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository
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
    fun provideProductRepository(productRealmDao: ProductRealmDao): ProductRepository {
        return ProductRepositoryImpl(productRealmDao)
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
    fun provideReportsRepository(reportsRealmDao: ReportsRealmDao, productRepository: ProductRepository): ReportsRepository {
        return ReportsRepositoryImpl(reportsRealmDao, productRepository)
    }

    @Provides
    fun provideMainFeedRepository(mainFeedService: MainFeedService): MainFeedRepository {
        return MainFeedRepositoryImpl(mainFeedService)
    }

    @Provides
    fun provideSalaryRepository(salaryRepository: SalaryRepository): SalaryRepository {
        return SalaryRepositoryImpl(salaryRepository)
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
}
