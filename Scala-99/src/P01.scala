object P01 {

  def lastBuiltin[A](ls: List[A]): A = ls.last

  def lastRecursive[A](ls: List[A]): A = ls match {
    case h :: Nil  => h
    case _ :: tail => lastRecursive(tail)
    case _         => throw new NoSuchElementException
  }
}
