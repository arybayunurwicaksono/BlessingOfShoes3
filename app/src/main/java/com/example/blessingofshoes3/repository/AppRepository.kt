package com.example.blessingofshoes3.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.blessingofshoes3.db.*
import com.example.blessingofshoes3.utils.executeThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class AppRepository @Inject constructor(application: Application) {
    private val mDbDao: DbDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDb.getDatabase(application)
        mDbDao = db.dbDao()
    }

    // database
    fun registerUser(users: Users){
        executorService.execute { mDbDao.registerUser(users) }
    }

    fun insertProduct(product: Product) {

        mDbDao.insertProduct(product)
        //return mDbDao.insertProduct(product)

    }

    fun insertAccounting(accounting: Accounting) {

        mDbDao.insertAccounting(accounting)
        //return mDbDao.insertProduct(product)

    }

    fun insertBalance(balance: Balance) {

        mDbDao.insertBalance(balance)
        //return mDbDao.insertProduct(product)

    }
    fun insertBalanceReport(balanceReport: BalanceReport) {

        mDbDao.insertBalanceReport(balanceReport)
        //return mDbDao.insertProduct(product)

    }
    fun insertRestock(restock: Restock) {

        mDbDao.insertRestock(restock)
        //return mDbDao.insertProduct(product)

    }
    fun insertCart(cart: Cart) {

        mDbDao.insertCart(cart)
        //return mDbDao.insertProduct(product)

    }

    fun insertTransaction(transaction: Transaction) {

        mDbDao.insertTransaction(transaction)
        //return mDbDao.insertProduct(product)

    }

    fun getUserInfo(email: String): Users = mDbDao.getUserInfo(email)

    fun getAllProduct(): LiveData<List<Product>> = mDbDao.getAllProduct()
    fun getAllServices(): LiveData<List<Services>> = mDbDao.getAllServices()
    fun getAllProductByName(nameProduct: String?): LiveData<List<Product>> = mDbDao.getAllProductByName(nameProduct)
    fun getAllProductOrderByTimeASC() : LiveData<List<Product>> = mDbDao.getAllProductOrderByTimeASC()
    fun getAllProductOrderByTimeDESC() : LiveData<List<Product>> = mDbDao.getAllProductOrderByTimeDESC()
    fun getAllProductOrderByPriceASC() : LiveData<List<Product>> = mDbDao.getAllProductOrderByPriceASC()
    fun getAllProductOrderByPriceDESC() : LiveData<List<Product>> = mDbDao.getAllProductOrderByPriceDESC()
    fun getAllProductOrderByBrand() : LiveData<List<Product>> = mDbDao.getAllProductOrderByBrand()
    fun getAllProductOrderBySize() : LiveData<List<Product>> = mDbDao.getAllProductOrderBySize()
    fun getAllProductOrderByNameASC() : LiveData<List<Product>> = mDbDao.getAllProductOrderByNameASC()
    fun getAllProductOrderByNameDESC() : LiveData<List<Product>> = mDbDao.getAllProductOrderByNameDESC()



    fun getAllBalanceReport(): LiveData<List<BalanceReport>> = mDbDao.getAllBalanceReport()
    fun getAllCartReport(): LiveData<List<Cart>> = mDbDao.getAllCartReport()
    fun getAllRestockReport(): LiveData<List<Restock>> = mDbDao.getAllRestockReport()
    fun getAllCartItemByStatus(): LiveData<List<Cart>> = mDbDao.getAllCartItemByStatus()
    fun getAllCartItemServices(customer: String) : LiveData<List<Cart>> = mDbDao.getAllCartItemServices(customer)
    fun getAllAccounting(): LiveData<List<Accounting>> = mDbDao.getAllAccounting()
    fun getAllTransaction(): LiveData<List<Transaction>> = mDbDao.getAllTransaction()
    fun getTransactionByMonth(getDate: String?): LiveData<List<Transaction>> = mDbDao.getTransactionByMonth(getDate)
    fun getTransactionByDay(getDate: String?): LiveData<List<Transaction>> = mDbDao.getTransactionByDay(getDate)
    fun getAllCartItem(): Flow<List<Cart>> = mDbDao.getAllCartItem()
    fun readUsername(email: String?): String = mDbDao.readUsername(email)
    fun checkTransaction(): Int? = mDbDao.checkTransaction()
    fun checkCart(): Int? = mDbDao.checkCart()
    /*    fun readCart(): ArrayList<Cart> = mDbDao.readCart()*/
    //fun readProductName(idProduct: Int): Product = mDbDao.readProductName(idProduct)
    fun readProductItem(idProduct: Int?): LiveData<Product> = mDbDao.readProductItem(idProduct)
    fun readUserDetail(email: String?): LiveData<Users> = mDbDao.readUserDetail(email)
    fun readCartItem(idItem: Int?): LiveData<Cart> = mDbDao.readCartItem(idItem)
    fun readDetailMonthlyAccounting(time: String?): LiveData<Accounting> = mDbDao.readDetailMonthlyAccounting(time)


    fun readTransactionById(idTransaction: Int?): LiveData<Transaction> = mDbDao.readTransactionById(idTransaction)
    fun readDigitalBalance(): Int? = mDbDao.readDigitalBalance()
    fun deleteProduct(idProduct: Int?) = mDbDao.deleteProduct(idProduct)
    fun deleteAccounting(idAccounting: Int?) = mDbDao.deleteAccounting(idAccounting)
    fun deleteCart(idItem: Int?) = mDbDao.deleteCart(idItem)
    fun deleteTransaction(idTransaction: Int?) = mDbDao.deleteTransaction(idTransaction)
    fun updateProductItem(data:Product) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateProductItem(data)
        }
    }
    fun updateUsersData(data:Users) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateUsersData(data)
        }
    }

    fun updateMonthlyAccounting(data:Accounting) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateMonthlyAccounting(data)
        }
    }

    fun updateCartStatus(status:String?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateCartStatus(status)
        }
    }
    fun updateCartStatusWashing(username:String?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateCartStatusWashing(username)
        }
    }
    fun updateCashBalance(cashValue:Int?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateCashBalance(cashValue)
        }
    }
    fun updateProfitBalance(profitValue:Int?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateProfitBalance(profitValue)
        }
    }
    fun updateDigitalBalance(digitalValue:Int?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateDigitalBalance(digitalValue)
        }
    }
    fun updateCartIdTransaction(idItem:Int?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateCartIdTransaction(idItem)
        }
    }
    fun updateCashOutBalance(total:Int?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateCashOutBalance(total)
        }
    }
    fun updateDigitalOutBalance(total:Int?) {
        CoroutineScope(Dispatchers.Main).launch {
            mDbDao.updateDigitalOutBalance(total)
        }
    }
    fun sumStockItem(idProduct: Int?, totalItem: Int?) = mDbDao.sumStockItem(idProduct, totalItem)
    fun sumTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?) = mDbDao.sumTotalPurchasesItem(idProduct, totalPurchases)
    fun sumCancelableStockItem(idProduct: Int?, totalItem: Int?) = mDbDao.sumCancelableStockItem(idProduct, totalItem)
    fun sumCancelableTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?) = mDbDao.sumCancelableTotalPurchasesItem(idProduct, totalPurchases)
    fun sumTotalPayment(): Int? = mDbDao.sumTotalPayment()
    fun sumTotalProfit(): Int? = mDbDao.sumTotalProfit()
    fun sumTotalProfitWashing(customer: String): Int? = mDbDao.sumTotalProfitWashing(customer)
    fun sumTotalTransaction(): Int? = mDbDao.sumTotalTransaction()
    fun sumTotalCompleteProfit(): Int? = mDbDao.sumTotalCompleteProfit()
    fun sumOldRealPrice(idProduct: Int?): Int? = mDbDao.sumOldRealPrice(idProduct)
    /*    fun deleteProductItem(data:Product) {
            CoroutineScope(Dispatchers.Main).launch {
                mDbDao.deleteProductItem(data)
            }
        }*/
    fun delete(product: Product) = executeThread {
        mDbDao.deleteProductItem(product)
    }
    fun readLastTransaction(): Int? = mDbDao.readLastTransaction()
    fun readTotalTransactionRecord(idTransaction: Int?): Int? = mDbDao.readTotalTransactionRecord(idTransaction)
    fun readLastProduct(): Int? = mDbDao.readLastProduct()


}