package com.example.blessingofshoes3.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blessingofshoes3.db.*
import com.example.blessingofshoes3.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class AppViewModel@Inject constructor(application: Application): ViewModel() {

    private lateinit var product: Product
    private val appRepository: AppRepository = AppRepository(application)
    val _productItem = MutableLiveData<List<Product>>()
    val productItem: LiveData<List<Product>> = _productItem

    val _userResponse = MutableLiveData<Users>()
    val userResponse: LiveData<Users> = _userResponse

    // database
    fun registerUser(users: Users) {
        appRepository.registerUser(users)
    }

    fun getUserInfo(email: String): Users = appRepository.getUserInfo(email)

    fun readUsername(email: String?): String = appRepository.readUsername(email)
    fun checkTransaction(): Int? = appRepository.checkTransaction()
    fun checkCart(): Int? = appRepository.checkCart()

    fun getAllProduct(): LiveData<List<Product>> = appRepository.getAllProduct()
    fun getAllServices(): LiveData<List<Services>> = appRepository.getAllServices()
    fun getAllProductByName(nameProduct: String?): LiveData<List<Product>> = appRepository.getAllProductByName(nameProduct)
    fun getAllProductOrderByTimeASC() : LiveData<List<Product>> = appRepository.getAllProductOrderByTimeASC()
    fun getAllProductOrderByTimeDESC() : LiveData<List<Product>> = appRepository.getAllProductOrderByTimeDESC()
    fun getAllProductOrderByPriceASC() : LiveData<List<Product>> = appRepository.getAllProductOrderByPriceASC()
    fun getAllProductOrderByPriceDESC() : LiveData<List<Product>> = appRepository.getAllProductOrderByPriceDESC()
    fun getAllProductOrderByBrand() : LiveData<List<Product>> = appRepository.getAllProductOrderByBrand()
    fun getAllProductOrderBySize() : LiveData<List<Product>> = appRepository.getAllProductOrderBySize()
    fun getAllProductOrderByNameASC() : LiveData<List<Product>> = appRepository.getAllProductOrderByNameASC()
    fun getAllProductOrderByNameDESC() : LiveData<List<Product>> = appRepository.getAllProductOrderByNameDESC()


    fun getAllBalanceReport(): LiveData<List<BalanceReport>> = appRepository.getAllBalanceReport()
    fun getAllCartReport(): LiveData<List<Cart>> = appRepository.getAllCartReport()
    fun getAllRestockReport(): LiveData<List<Restock>> = appRepository.getAllRestockReport()
    fun getAllCartItemByStatus(): LiveData<List<Cart>> = appRepository.getAllCartItemByStatus()
    fun getAllCartItemServices(customer: String) : LiveData<List<Cart>> = appRepository.getAllCartItemServices(customer)
    fun getAllAccounting(): LiveData<List<Accounting>> = appRepository.getAllAccounting()
    fun getAllTransaction(): LiveData<List<Transaction>> = appRepository.getAllTransaction()
    fun getTransactionByMonth(getDate: String?): LiveData<List<Transaction>> = appRepository.getTransactionByMonth(getDate)
    fun getTransactionByDay(getDate: String?): LiveData<List<Transaction>> = appRepository.getTransactionByDay(getDate)
    fun getAllCartItem(): Flow<List<Cart>> = appRepository.getAllCartItem()

    fun insertProduct(product: Product) {
        appRepository.insertProduct(product)
    }

    fun insertAccounting(accounting: Accounting) {
        appRepository.insertAccounting(accounting)
    }

    fun insertBalance(balance: Balance) {
        appRepository.insertBalance(balance)
    }
    fun insertBalanceReport(balanceReport: BalanceReport) {
        appRepository.insertBalanceReport(balanceReport)
    }
    fun insertRestock(restock: Restock) {
        appRepository.insertRestock(restock)
    }

    fun insertCart(cart: Cart) {
        appRepository.insertCart(cart)
    }
    fun insertTransaction(transaction: Transaction) {
        appRepository.insertTransaction(transaction)
    }

/*    fun updateProduct(idProduct: Int, nameProduct:String, priceProduct:String, stockProduct:String, productPhoto: Bitmap) {
        appRepository.updateProduct(idProduct, nameProduct, priceProduct, stockProduct, productPhoto)
    }*/

    //fun readProductName(idProduct: Int): Product = appRepository.readProductName(idProduct)
    fun sumTotalPayment() : Int? = appRepository.sumTotalPayment()
    fun sumTotalProfit() : Int? = appRepository.sumTotalProfit()
    fun sumTotalProfitWashing(customer: String): Int? = appRepository.sumTotalProfitWashing(customer)
    fun sumTotalTransaction() : Int? = appRepository.sumTotalTransaction()
    fun sumTotalCompleteProfit() : Int? = appRepository.sumTotalCompleteProfit()
    fun sumStockItem(idProduct: Int?, totalItem: Int?) = appRepository.sumStockItem(idProduct, totalItem)
    fun sumTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?) = appRepository.sumTotalPurchasesItem(idProduct, totalPurchases)
    fun sumCancelableStockItem(idProduct: Int?, totalItem: Int?) = appRepository.sumCancelableStockItem(idProduct, totalItem)
    fun sumCancelableTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?) = appRepository.sumCancelableTotalPurchasesItem(idProduct, totalPurchases)
    fun sumOldRealPrice(idProduct: Int?) : Int? = appRepository.sumOldRealPrice(idProduct)
    fun readProductItem(idProduct: Int?): LiveData<Product> = appRepository.readProductItem(idProduct)
    fun readUserDetail(email: String?): LiveData<Users> = appRepository.readUserDetail(email)
    fun readDetailMonthlyAccounting(time: String?): LiveData<Accounting> = appRepository.readDetailMonthlyAccounting(time)
    fun readCartItem(idItem: Int?): LiveData<Cart> = appRepository.readCartItem(idItem)

    fun readTransactionById(idTransaction: Int?): LiveData<Transaction> = appRepository.readTransactionById(idTransaction)
    fun readDigitalBalance(): Int? = appRepository.readDigitalBalance()
    fun deleteProduct(idProduct: Int?) = appRepository.deleteProduct(idProduct)
    fun deleteAccounting(idAccounting: Int?) = appRepository.deleteAccounting(idAccounting)
    fun deleteCart(idCart: Int?) = appRepository.deleteCart(idCart)
    fun deleteTransaction(idTransaction: Int?) = appRepository.deleteTransaction(idTransaction)
    fun updateProductItem(context: Context, idProduct:Int, nameProduct:String, brandProduct:String,
                          priceProduct:Int, stockProduct:Int, sizeProduct:String, realPriceProduct:Int, totalPurchases:Int,
                          profitProduct:Int, productPhoto: Bitmap, username: String, timeAdded:String,
                          onSuccess: (Boolean) -> Unit) {
        appRepository.updateProductItem(Product(idProduct, nameProduct, brandProduct,
            priceProduct, stockProduct, sizeProduct, realPriceProduct, totalPurchases, profitProduct, productPhoto, username, timeAdded))
        onSuccess(true)
    }

    fun updateUsersData(context: Context, idUser:Int, fullname:String, username:String,
                          email:String, password:String, role:String, photoUser: Bitmap,
                          onSuccess: (Boolean) -> Unit) {
        appRepository.updateUsersData(Users(idUser, fullname, username,
            email, password, role, photoUser))
        onSuccess(true)
    }

    fun updateMonthlyAccounting(
        context: Context,
        idAccounting: Int,
        dateAccounting: String?,
        initDigital: Int?,
        initCash: Int?,
        initStock: Int?,
        initWorth: Int?,
        capitalInvest: Int?,
        incomeTransaction: Int?,
        transactionItem: Int?,
        restockPurchases: Int?,
        restockItem: Int?,
        returnTotal: Int?,
        returnItem: Int?,
        finalDigital: Int?,
        finalCash: Int?,
        finalStock: Int?,
        finalWorth: Int?,
        otherNeeds: Int?,
        username: String,
        status: String,
        balanceIn: Int?,
        balanceOut: Int?,
        profitEarned: Int?,
        onSuccess: (Boolean) -> Unit) {
        appRepository.updateMonthlyAccounting(
            Accounting(
                idAccounting,
                dateAccounting,
                initDigital,
                initCash,
                initStock,
                initWorth,
                capitalInvest,
                incomeTransaction,
                transactionItem,
                restockPurchases,
                restockItem,
                returnTotal,
                returnItem,
                finalDigital,
                finalCash,
                finalStock,
                finalWorth,
                otherNeeds,
                username,
                status,
                balanceIn,
                balanceOut,
                profitEarned))
        onSuccess(true)
    }

    fun updateCartStatus(context: Context, status: String?,
                         onSuccess: (Boolean) -> Unit) {
        appRepository.updateCartStatus(status)
        onSuccess(true)
    }

    fun updateCartStatusWashing(context: Context, username: String?,
                         onSuccess: (Boolean) -> Unit) {
        appRepository.updateCartStatusWashing(username)
        onSuccess(true)
    }

    fun updateCashBalance(context: Context, cashValue: Int?,
                          onSuccess: (Boolean) -> Unit) {
        appRepository.updateCashBalance(cashValue)
        onSuccess(true)
    }
    fun updateProfitBalance(context: Context, profitValue: Int?,
                          onSuccess: (Boolean) -> Unit) {
        appRepository.updateProfitBalance(profitValue)
        onSuccess(true)
    }

    fun updateDigitalBalance(context: Context, digitalValue: Int?,
                             onSuccess: (Boolean) -> Unit) {
        appRepository.updateDigitalBalance(digitalValue)
        onSuccess(true)
    }
    fun updateCartIdTransaction(context: Context, idItem: Int?,
                                onSuccess: (Boolean) -> Unit) {
        appRepository.updateCartIdTransaction(idItem)
        onSuccess(true)
    }
    fun updateCashOutBalance(context: Context, total: Int?,
                             onSuccess: (Boolean) -> Unit) {
        appRepository.updateCashOutBalance(total)
        onSuccess(true)
    }
    fun updateDigitalOutBalance(context: Context, total: Int?,
                                onSuccess: (Boolean) -> Unit) {
        appRepository.updateDigitalOutBalance(total)
        onSuccess(true)
    }

    /*fun deleteProductItem(context: Context?, data:Product, onSuccess: (Boolean) -> Unit) {
        appRepository.deleteProductItem(data)
        onSuccess(true)
    }*/
    fun delete(product: Product) {
        appRepository.delete(product)
    }
    fun getData(): Product = product

    fun readLastTransaction(): Int? = appRepository.readLastTransaction()
    fun readTotalTransactionRecord(idTransaction: Int?): Int? = appRepository.readTotalTransactionRecord(idTransaction)
    fun readLastProduct(): Int? = appRepository.readLastProduct()
}
