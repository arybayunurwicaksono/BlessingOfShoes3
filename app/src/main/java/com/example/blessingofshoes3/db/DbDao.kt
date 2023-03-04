package com.example.blessingofshoes3.db

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DbDao {

    @Query("SELECT * FROM users WHERE email LIKE :email")
    fun getUserInfo(email: String) : Users

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun registerUser(users: Users)

    @Query("SELECT * FROM users WHERE email LIKE :email AND password LIKE :password")
    fun readDataUser(email: String, password: String): Users

    @Query("SELECT email FROM users WHERE email=:email")
    fun validateEmail(email: String) : String

    @Query("SELECT COUNT(idUser) FROM users")
    fun validateOwner() : Int

    @Query("SELECT username FROM users WHERE username=:username")
    fun validateUsername(username: String) : String

    @Query("SELECT username FROM users WHERE email LIKE :email")
    fun readUsername(email: String?) : String

    @Query("SELECT fullname FROM users WHERE email LIKE :email")
    fun readFullname(email: String?) : String

    @Query("SELECT email FROM users WHERE email LIKE :email")
    fun readEmail(email: String?) : String

    @Query("SELECT password FROM users WHERE email LIKE :email")
    fun readPassword(email: String?) : String

    @Query("SELECT photoUser FROM users WHERE email LIKE :email")
    fun readPhoto(email: String?) : Bitmap

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertProduct(product: Product)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertServices(services: Services)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertReturn(returnTable: Return)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAccounting(accounting: Accounting)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBalance(balance: Balance)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBalanceReport(balanceReport: BalanceReport)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRestock(restock: Restock)

    @Query("SELECT * FROM balanceReport")
    fun getAllBalanceReport() : LiveData<List<BalanceReport>>

    @Query("SELECT * FROM cart WHERE status LIKE 'complete'")
    fun getAllCartReport() : LiveData<List<Cart>>

    @Query("SELECT * FROM restock")
    fun getAllRestockReport() : LiveData<List<Restock>>

    @Query("SELECT * FROM product")
    fun getAllProduct() : LiveData<List<Product>>

    @Query("SELECT * FROM services")
    fun getAllServices() : LiveData<List<Services>>

    @Query("SELECT * FROM product WHERE nameProduct Like :nameProduct")
    fun getAllProductByName(nameProduct: String?) : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY idProduct ASC")
    fun getAllProductOrderByTimeASC() : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY idProduct DESC")
    fun getAllProductOrderByTimeDESC() : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY priceProduct ASC")
    fun getAllProductOrderByPriceASC() : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY priceProduct DESC")
    fun getAllProductOrderByPriceDESC() : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY brandProduct ASC")
    fun getAllProductOrderByBrand() : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY sizeProduct ASC")
    fun getAllProductOrderBySize() : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY nameProduct ASC")
    fun getAllProductOrderByNameASC() : LiveData<List<Product>>

    @Query("SELECT * FROM product ORDER BY nameProduct DESC")
    fun getAllProductOrderByNameDESC() : LiveData<List<Product>>

    @Query("SELECT * FROM cart WHERE status LIKE 'onProgress'")
    fun getAllCartItemByStatus() : LiveData<List<Cart>>

    @Query("SELECT * FROM cart WHERE status LIKE 'onWashing' AND username LIKE :customer")
    fun getAllCartItemServices(customer: String) : LiveData<List<Cart>>

    @Query("SELECT * FROM accounting")
    fun getAllAccounting() : LiveData<List<Accounting>>

    @Query("SELECT * FROM cart")
    fun getAllCartItem() : Flow<List<Cart>>

    @Query("SELECT count(idProduct) FROM product WHERE idProduct LIKE 0")
    fun validateWashingServicesItem() : Int

    @Query("UPDATE cart SET status = :status WHERE status = 'onProgress'")
    fun updateCartStatus(status: String?)

    @Query("UPDATE cart SET status = 'complete' WHERE username = :username")
    fun updateCartStatusWashing(username: String?)

    @Query("UPDATE balance SET CashBalance = (SELECT SUM(CashBalance + :cashValue) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateCashBalance(cashValue: Int?)

    @Query("UPDATE balance SET Profit = (SELECT SUM(Profit + :profitValue) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateProfitBalance(profitValue: Int?)

    @Query("UPDATE balance SET digitalBalance = (SELECT SUM(digitalBalance + :digitalValue) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateDigitalBalance(digitalValue: Int?)

    @Query("UPDATE cart SET idTransaction = :idItem WHERE idTransaction = 0")
    fun updateCartIdTransaction(idItem: Int?)

    @Query("SELECT * FROM cart WHERE status LIKE :status")
    fun getCartByStatus(status: String?) : Flow<List<Cart>>
/*    @Query("UPDATE product SET nameProduct =:nameProduct, brandProduct =:brandProduct, priceProduct =:priceProduct, " +
            "stockProduct =:stockProduct, sizeProduct=:sizeProduct, realPriceProduct=:realPriceProduct, " +
            "profitProduct = profitProduct, productPhoto =:productPhoto WHERE idProduct =:idProduct LIKE :idProduct")
    fun updateProduct(idProduct: Int?, nameProduct:String, brandProduct:String, priceProduct:String,
                      stockProduct:Int?, sizeProduct:String, realPriceProduct:String, profitProduct:Int?, productPhoto: Bitmap)*/

    @Query("SELECT * FROM users WHERE email LIKE :email")
    fun readUserDetail(email: String?): LiveData<Users>

    @Query("SELECT * FROM product WHERE idProduct LIKE :idProduct")
    fun readProductItem(idProduct: Int?): LiveData<Product>

    @Query("SELECT * FROM accounting WHERE dateAccounting LIKE :time")
    fun readDetailMonthlyAccounting(time: String?): LiveData<Accounting>

    //UPDATE Products set Price = (SELECT SUM(Price-7) FROM Products WHERE ProductId = 7) WHERE ProductID = 7
    @Query("UPDATE Product set stockProduct = (SELECT SUM(stockProduct- :totalItem) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumStockItem(idProduct: Int?, totalItem: Int?)

    @Query("UPDATE Product set stockProduct = (SELECT SUM(stockProduct+ :totalItem) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumCancelableStockItem(idProduct: Int?, totalItem: Int?)

    @Query("UPDATE Product set totalPurchases = (SELECT SUM(totalPurchases- :totalPurchases) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?)

    @Query("UPDATE Product set totalPurchases = (SELECT SUM(totalPurchases+ :totalPurchases) FROM product WHERE idProduct LIKE :idProduct) WHERE idProduct = :idProduct")
    fun sumCancelableTotalPurchasesItem(idProduct: Int?, totalPurchases: Int?)

    //@Query("SELECT nameProduct FROM product WHERE idProduct LIKE :idProduct LIKE :idProduct")
    //fun readProductName(idProduct: Int): Product

    @Query("SELECT SUM(totalpayment) FROM cart WHERE status = 'onProgress'")
    fun sumTotalPayment(): Int?

    @Query("SELECT SUM(totalpayment) FROM cart WHERE status = 'onWashing' AND username = :customer")
    fun sumTotalPaymentWashing(customer: String): Int?

    @Query("SELECT SUM(totalTransaction) FROM `transaction`")
    fun sumTotalTransaction(): Int?

    @Query("SELECT SUM(`totalProfit `) FROM cart WHERE status = 'onProgress'")
    fun sumTotalProfit(): Int?

    @Query("SELECT SUM(`totalProfit `) FROM cart WHERE status = 'onWashing' AND username = :customer")
    fun sumTotalProfitWashing(customer: String): Int?

    @Query("SELECT SUM(profitItem) FROM cart WHERE status = 'complete'")
    fun sumTotalCompleteProfit(): Int?

    @Query("SELECT SUM(realPriceProduct*stockProduct) FROM product WHERE idProduct = :idProduct")
    fun sumOldRealPrice(idProduct: Int?): Int?

    /*@Query("SELECT idTransaction FROM `transaction` WHERE idTransaction IN (SELECT MAX(idTransaction) FROM `transaction`)")*/
    @Query("SELECT idTransaction FROM `transaction` ORDER BY idTransaction DESC LIMIT 1")
    fun readLastTransaction(): Int?

    @Query("SELECT idProduct FROM `product` ORDER BY idProduct DESC LIMIT 1")
    fun readLastProduct(): Int?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCart(cart: Cart)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTransaction(transaction: com.example.blessingofshoes3.db.Transaction)

    @Update
    fun updateProductItem(data: Product)

    @Update
    fun updateUsersData(data: Users)

    @Update
    fun updateMonthlyAccounting(data: Accounting)

    @Delete
    fun deleteProductItem(data: Product)

    @Query("DELETE FROM product WHERE idProduct LIKE :idProduct")
    fun deleteProduct(idProduct: Int?)

    @Query("DELETE FROM accounting WHERE idAccounting LIKE :idAccounting")
    fun deleteAccounting(idAccounting: Int?)

    @Query("DELETE FROM cart WHERE idItem LIKE :idItem")
    fun deleteCart(idItem: Int?)

    @Query("SELECT COUNT(idTransaction) FROM `transaction`")
    fun checkTransaction(): Int?

    @Query("SELECT COUNT(idBalanceReport) FROM `balanceReport` where reportTag LIKE 'capital'")
    fun checkCapital(): Int?

    @Query("SELECT COUNT(idItem) FROM `cart` WHERE status LIKE 'onProgress'")
    fun checkCart(): Int?

    @Query("SELECT COUNT(idItem) FROM `cart` WHERE status LIKE 'onWashing' AND username LIKE :customer")
    fun checkCartWashing(customer: String): Int?

    @Query("DELETE FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun deleteTransaction(idTransaction: Int?)

/*    @Query("SELECT SUM(idTransaction+1) FROM Cart WHERE idTransaction - (SELECT idTransaction FROM Cart ORDER BY idTransaction DESC LIMIT 1)")
    fun testCart(): Int?*/

    @Query("SELECT status FROM cart WHERE status LIKE :status LIMIT 1")
    fun testCart(status: String?): String?

    @Query("SELECT * FROM `transaction`")
    fun getAllTransaction() : LiveData<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE transactionDate LIKE :getDate")
    fun getTransactionByMonth(getDate: String?) : LiveData<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE transactionDate LIKE :getDate")
    fun getTransactionByDay(getDate: String?) : LiveData<List<Transaction>>

    @Query("SELECT * FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTransactionById(idTransaction: Int?): LiveData<com.example.blessingofshoes3.db.Transaction>

    @Query("SELECT digitalBalance FROM `balance` WHERE idBalance LIKE 1")
    fun readDigitalBalance(): Int?

    @Query("SELECT profit FROM `balance` WHERE idBalance LIKE 1")
    fun readProfitBalance(): Int?

    @Query("SELECT CashBalance FROM `balance` WHERE idBalance LIKE 1")
    fun readCashBalance(): Int?

    @Query("UPDATE balance SET CashBalance = (SELECT SUM(CashBalance - :total) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateCashOutBalance(total: Int?)

    @Query("UPDATE balance SET digitalBalance = (SELECT SUM(digitalBalance - :total) FROM balance WHERE idBalance = 1) WHERE idBalance = 1")
    fun updateDigitalOutBalance(total: Int?)

    @Query("SELECT * FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete'")
    fun readTransactionItem(idTransaction: String?) : Flow<List<Cart>>

    @Query("SELECT * FROM restock")
    fun getRestockData() : Flow<List<Restock>>

    @Query("SELECT * FROM balanceReport")
    fun getBalanceReportData() : Flow<List<BalanceReport>>

    //RECEIPT
    @Query("SELECT COUNT(idTransaction) FROM cart WHERE idTransaction LIKE :idTransaction")
    fun readTotalTransactionRecord(idTransaction: Int?) : Int?

    @Query("SELECT COUNT(username) FROM cart WHERE username LIKE :customer")
    fun readTotalTransactionServiceRecord(customer: String) : Int?

    @Query("SELECT nameItem FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readNameItemReceipt(idTransaction: Int?, recordPosition: Int?) : String

    @Query("SELECT nameItem FROM cart WHERE username LIKE :username AND status LIKE 'onWashing' ORDER BY idItem LIMIT :recordPosition,1")
    fun readNameItemServiceReceipt(username: String?, recordPosition: Int?) : String

    @Query("SELECT totalItem FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readTotalItemReceipt(idTransaction: Int?, recordPosition: Int?) : Int

    @Query("SELECT totalItem FROM cart WHERE username LIKE :username AND status LIKE 'onWashing' ORDER BY idItem LIMIT :recordPosition,1")
    fun readTotalItemWashingReceipt(username: String, recordPosition: Int?) : Int

    @Query("SELECT priceItem FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readPriceItemReceipt(idTransaction: Int?, recordPosition: Int?) : Int

    @Query("SELECT priceItem FROM cart WHERE username LIKE :customer AND status LIKE 'onWashing' ORDER BY idItem LIMIT :recordPosition,1")
    fun readPriceItemServiceReceipt(customer: String, recordPosition: Int?) : Int

    @Query("SELECT totalpayment FROM cart WHERE idTransaction LIKE :idTransaction AND status LIKE 'complete' ORDER BY idItem LIMIT :recordPosition,1")
    fun readTotalPaymentReceipt(idTransaction: Int?, recordPosition: Int?) : Int

    @Query("SELECT totalpayment FROM cart WHERE username LIKE :username AND status LIKE 'onWashing' ORDER BY idItem LIMIT :recordPosition,1")
    fun readTotalPaymentServiceReceipt(username: String, recordPosition: Int?) : Int

    @Query("SELECT totalTransaction FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTotalTransactionPayment(idTransaction: Int?) : Int

    @Query("SELECT SUM(totalpayment) FROM `cart` WHERE username LIKE :username AND status LIKE 'onWashing'")
    fun readTotalTransactionServicePayment(username: String) : Int

    @Query("SELECT moneyReceived FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTotalReceivedPayment(idTransaction: Int?) : Int

    @Query("SELECT moneyChange FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun readTotalChangePayment(idTransaction: Int?) : Int

    //accounting
    @Query("SELECT sum(profitTransaction) FROM 'transaction' WHERE transactionDate LIKE :time")
    fun sumTotalProfitAcc(time: String?): Int?

    @Query("SELECT COUNT(idProduct) FROM product")
    fun validateCountProduct(): Int?

    @Query("SELECT COUNT(idBalanceReport) FROM balanceReport")
    fun validateCountBalance(): Int?

    @Query("SELECT COUNT(dateAccounting) FROM accounting WHERE dateAccounting LIKE :datePicker")
    fun validateAccounting(datePicker : String?): Int?

    @Query("SELECT COUNT(idTransaction) FROM `transaction` WHERE transactionDate LIKE :datePicker")
    fun validateTransaction(datePicker : String?): Int?

    @Query("SELECT COUNT(idRestock) FROM `restock` WHERE restockDate LIKE :datePicker")
    fun validateRestock(datePicker : String?): Int?

    @Query("SELECT COUNT(idReturn) FROM `return` WHERE returnDate LIKE :datePicker")
    fun validateReturn(datePicker : String?): Int?

    @Query("SELECT sum(totalPurchases) FROM product")
    fun readTotalStockWorth(): Int?

    @Query("SELECT sum(stockProduct) FROM product")
    fun readTotalStock(): Int?

    @Query("SELECT SUM(totalBalance) FROM balanceReport WHERE reportTag  LIKE 'Capital' AND timeAdded LIKE '%' ||  :timeAdded ||  '%'")
    fun sumTotalInvest(timeAdded: String?): Int?

    @Query("SELECT SUM(totalBalance) FROM balanceReport WHERE status  LIKE 'Out' AND timeAdded LIKE :timeAdded ")
    fun sumTotalBalanceOut(timeAdded: String?): Int?

    @Query("SELECT SUM(totalTransaction) FROM `transaction` WHERE transactionDate LIKE :time ")
    fun sumTotalTransactionAcc(time: String?): Int?

    @Query("SELECT SUM(totalItem) FROM `cart` WHERE status LIKE 'onProgress' ")
    fun sumTotalTransactionItem(): Int?

    @Query("SELECT SUM(totalItem) FROM `cart` WHERE status = 'onWashing' AND username = :customer")
    fun sumTotalTransactionItemWashing(customer: String): Int?

    @Query("SELECT SUM(totalItem) FROM `transaction` WHERE transactionDate LIKE :time AND transactionType LIKE 'product' ")
    fun readTotalTransactionItem(time: String?): Int?

    @Query("SELECT SUM(totalPurchases) FROM `restock` WHERE restockDate LIKE :time ")
    fun sumTotalPurchases(time: String?): Int?

    @Query("SELECT SUM(stockAdded) FROM `restock` WHERE restockDate LIKE :time ")
    fun sumTotalStockAdded(time: String?): Int?

    @Query("SELECT SUM(totalRefund) FROM `return` WHERE returnDate LIKE :time ")
    fun sumTotalRefund(time: String?): Int?

    @Query("SELECT SUM(totalItem) FROM `return` WHERE returnDate LIKE :time ")
    fun sumTotalRefundItem(time: String?): Int?

    @Query("SELECT COUNT(nameProduct) FROM `product` WHERE nameProduct LIKE :nameProduct")
    fun validateProductName(nameProduct: String?): Int?

    @Query("SELECT COUNT(idItem) FROM `cart` WHERE username LIKE :customer")
    fun validateCustomer(customer: String?): Int?


    //return
    @Query("SELECT COUNT(idTransaction) FROM `cart` WHERE idTransaction LIKE :idTransaction")
    fun validateReturnIdTransaction(idTransaction: Int?): Int?

    @Query("UPDATE cart SET totalpayment = (SELECT SUM(totalpayment - :totalPayment) FROM cart WHERE idItem LIKE :idItem) WHERE idItem LIKE :idItem")
    fun updateCartTotalPaymentOnReturn(totalPayment: Int?, idItem: Int?)

    @Query("UPDATE cart SET `totalProfit ` = (SELECT SUM(`totalProfit ` - :totalProfit) FROM cart WHERE idItem LIKE :idItem) WHERE idItem LIKE :idItem")
    fun updateCartTotalProfitOnReturn(totalProfit: Int?, idItem: Int?)

    @Query("UPDATE cart SET totalItem = (SELECT SUM(totalItem - :totalItem) FROM cart WHERE idItem LIKE :idItem) WHERE idItem LIKE :idItem")
    fun updateCartTotalItemOnReturn(totalItem: Int?, idItem: Int?)

    @Query("SELECT totalpayment FROM cart WHERE idItem LIKE :idItem")
    fun validateItemOnReturn(idItem: Int?): Int?

    @Query("SELECT totalTransaction FROM `transaction` WHERE idTransaction LIKE :idTransaction")
    fun validateTransactionOnReturn(idTransaction: Int?): Int?

    @Query("UPDATE `transaction` SET totalTransaction = (totalTransaction - :totalPayment) WHERE idTransaction LIKE :idTransaction")
    fun updateTransactionTotalPaymentOnReturn(totalPayment: Int?, idTransaction: Int?)

    @Query("UPDATE `transaction` SET profitTransaction = (profitTransaction - :totalProfit) WHERE idTransaction LIKE :idTransaction")
    fun updateTransactionTotalProfitOnReturn(totalProfit: Int?, idTransaction: Int?)

    @Query("UPDATE `transaction` SET totalItem = (totalItem - :totalItem) WHERE idTransaction LIKE :idTransaction")
    fun updateTransactionTotalItemOnReturn(totalItem: Int?, idTransaction: Int?)

    @Query("UPDATE `transaction` SET moneyChange = (moneyChange + :totalItem) WHERE idTransaction LIKE :idTransaction")
    fun updateTransactionChangeOnReturn(totalItem: Int?, idTransaction: Int?)

    @Query("SELECT * FROM cart WHERE idItem LIKE :idItem")
    fun readCartItem(idItem: Int?): LiveData<Cart>

    @Query("SELECT productPhoto FROM cart WHERE idItem LIKE :idItem")
    fun readCartPhoto(idItem: Int?): Bitmap

}