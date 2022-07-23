package com.rockthejvm

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

object MonadsForBeginners {

  case class SafeValue[+T](private val internalValue: T) { // "constructor" = pure, or unit
    def get: T = synchronized {
      // does something interesting
      internalValue
    }

    def flatMap[S](transformer: T => SafeValue[S]): SafeValue[S] = synchronized{ // bind, or flatmap
      transformer(internalValue)
    }
  }

  // "external" API
  def gimmeSafeValue[T] (value: T): SafeValue[T] = SafeValue(value)

  val safeString: SafeValue[String] = gimmeSafeValue("Scala is awesome")
  // extract
  val string: String = safeString.get
  // transform
  val upperString: String = string.toUpperCase()
  // wrap
  val upperSafeString = SafeValue(upperString)
  // ETW

  // compressed:
  val upperSafeString2 = safeString.flatMap(s => SafeValue(s.toUpperCase()))

  // Examples

  // Example 1 : census
  case class Person(firstName: String, lastName: String) {
    assert(firstName != null && lastName != null)
  }

  // census API
  def getPerson(firstName: String, lastName: String): Person =
    if (firstName != null) {
      if (lastName != null) {
        Person(firstName, lastName)
      } else {
        null
      }
    } else {
      null
    }

  def getPersonBetter(firstName: String, lastName: String): Option[Person] =
    Option(firstName).flatMap { fName =>
      Option(lastName).flatMap { lName =>
        Option(Person(fName, lName))
      }
    }

  def getPersonFor(firstName: String, lastName: String): Option[Person] = for {
    fName <- Option(firstName)
    lName <- Option(lastName)
  } yield Person(fName, lName)

  // Example 2: asynchronous fetches

  case class User(id: String)
  case class Product(sku: String, price: Double)

  def getUser(url:String): Future[User] = Future {
    User("daniel") // sample
  }

  def getLastOrder(userId: String): Future[Product] = Future {
    Product("123-456", 99.99) // sample
  }

  val danielsUrl = "my.store.com/users/daniel"

  // ETW
  getUser(danielsUrl).onComplete {
    case Success(User(id)) =>
      val lastOrder = getLastOrder(id)
      lastOrder.onComplete( {
        case Success(Product(sku, p)) =>
          val vatIncludedPrice = p * 1.19
        // pass it on - send Daniel an email
      })
  }

  val vatInclPrice: Future[Double] = getUser(danielsUrl)
    .flatMap(user => getLastOrder(user.id))
    .map(_.price * 1.19)

  val valInclPriceFor: Future[Double] = for {
    user <- getUser(danielsUrl)
    product <- getLastOrder(user.id)
  } yield product.price * 1.19

  // Example:3 double-for loops

  val numbers = List(1, 2, 3)
  val chars = List('a', 'b', 'c')
  // flatMaps
  val checkerboard: List[(Int, Char)] = numbers.flatMap(number => chars.map(char => (number, char)))
  val checkerboard2 = for {
    number <- numbers
    char <- chars
  } yield (number, char)

  // Properties

  // prop 1
  def twoConsecutive(x: Int) = List(x, x + 1)
  twoConsecutive(3) // List(3, 4)
  List(3).flatMap(twoConsecutive) // List(3, 4)
  // Monad(v).flatMap(f) == f(x)

  // prop 2
  List(1, 2, 3).flatMap(x => List(x)) // List(1, 2, 3)
  // Monad(v).flatMap(x => Monad(x)) USELESS, returns Monad(v)

  // prop 3 - ETW-ETW
  val incrementer = (x: Int) => List(x, x + 1)
  val doubler = (x: Int) => List(x, 2 * x)

  def main(args: Array[String]): Unit = {
    println(
      numbers.flatMap(incrementer).flatMap(doubler) == numbers.flatMap(x => incrementer(x).flatMap(doubler))
    )
    // List(1, 2, 3, 4,   2, 4, 3, 6,   3, 6, 4, 8)
    /*
      List(
        incrementer(1).flatMap(doubler) -- 1,2,2,4
        incrementer(2).flatMap(doubler) -- 2,4,3,6
        incrementer(2).flatMap(doubler) -- 3,6,4,8
      )

      Monad(v).flatMap(f).flatMap(g) == Monad(v).flatMap(x => f(x).flatMap(g))
     */
  }







}
