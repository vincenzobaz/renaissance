package org.renaissance

import java.net.URLClassLoader
import java.util.Optional

class ProxyRenaissanceBenchmark(
  val groupClassLoader: URLClassLoader,
  val benchmarkClassName: String
) extends RenaissanceBenchmarkApi {
  val benchmarkClass = groupClassLoader.loadClass(benchmarkClassName)
  val benchmark = benchmarkClass.newInstance

  private def call[R](name: String, arguments: Seq[Object]): R = {
    benchmarkClass
      .getMethod(name, arguments.toArray.map(_.getClass): _*)
      .invoke(benchmark, arguments.toArray: _*)
      .asInstanceOf[R]
  }

  private def dup(c: Config): Object = {
    val cls = groupClassLoader.loadClass("org.renaissance.Config")
    val tc = cls.newInstance.asInstanceOf[Object]
    cls.getDeclaredField("benchmarkList").set(tc, c.benchmarkList())
    cls.getDeclaredField("repetitions").set(tc, c.repetitions())
    cls.getDeclaredField("plugins").set(tc, c.plugins())
    cls.getDeclaredField("policy").set(tc, c.policy())
    cls.getDeclaredField("readme").set(tc, c.readme())
    tc
  }

  override def description(): String = call("description", Seq())

  override def name(): String = call("name", Seq())

  override def mainGroup(): String = call("mainGroup", Seq())

  override def defaultRepetitions(): Int = call("defaultRepetitions", Seq())

  override def initialRelease(): Optional[String] = call("initialRelease", Seq())

  override def finalRelease(): Optional[String] = call("finalRelease", Seq())

  override def setUpBeforeAll(c: Config): Unit = call("setUpBeforeAll", Seq(dup(c)))

  override def tearDownAfterAll(c: Config): Unit = call("tearDownAfterAll", Seq(dup(c)))

  override def beforeIteration(c: Config): Unit = call("beforeIteration", Seq(dup(c)))

  override def afterIteration(c: Config): Unit = call("afterIteration", Seq(dup(c)))

  override def runBenchmark(c: Config): Optional[Throwable] = call("runBenchmark", Seq(dup(c)))
}
