package exam.sample.fomula1

import exam.sample.fomula1.model.Track


object Formula1Game extends App {

  if (args.size < 2) {
    throw new IllegalArgumentException("length of the track and teams number are required")
  }
  startGame(args(0).toDouble, args(1).toInt)


  def startGame(tracklen: Double, teamNumber: Int) = {

    val track = new Track(tracklen, teamNumber)
    println(track)
    var time = 0
    while (track.findWinner().isEmpty) {

      track.updateLoop()

      time = time + 2

    }
    println("########################################################")
    println("################  FINISH    ############################")

    println("########################################################")
    println("################   WINNER   ############################")
    track.findWinner().foreach(println)
    println("################   RACE TIME   ############################")
    println(s"\t \t $time seconds \n\n")

    println("########################################################")
    println("################Cars table ############################")
    track.cars.sortBy(_.distance).reverse.zipWithIndex.foreach { case (car, position) => print(s"${position + 1}. "); println(car)}

  }


}
