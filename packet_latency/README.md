# 今回はbyte型を使うとかふざけたことはしません。

#動作方法
controller5.javaを起動すれば動くと思います。

#デバッグモード
デッドロックが発生しているか確認するもの
普段と違う挙動をしているかもしれません（正常な動作は未確認）


## キューの数は次数+1
最後は計算ノードから送信する部分


#Node.java Run部分計画
# next -> ready信号の制御部分作成


#node.ready[i]
送信先のキューが埋まっているかそうでないか確認する



#問題点
NKと同じやり方でいいのか
数字よりもn番目と交換のほうがいい気がする…が
NKと比較しにくくなるので統一することにしました。

#Cyclemerge-NKについて考察してみる
サイクルのリストと残りのもの(remainder)の２つについて作成する
条件としてPermutationの数が少なくなるようにする(ただし、サイクル数１のものを除外する)
コードどうしたらいいのか考え中。→デッドロック回避できなかったため、却下されました。
