package abc.abc450

fun main() {
    val (h,w)  = readln().split(" ").map{it.toInt()}
    val grid = Array(h+2){CharArray(w+2)}
    for(i in 1 until h+1){
        val tmp = readln().toCharArray()
        for(j in 1 until w+1){
            grid[i][j] = tmp[j-1]
        }
    }

    var ans = 0
    val visited = Array(h+2){BooleanArray(w+2){false} }
    for(i in 1 until h+1){
        for(j in 1 until w+1){
            if(grid[i][j] == '.' && !visited[i][j]){
                if(dfs(visited,grid,i,j,ans,h,w) == true) {

                    ans++
                }
            }
        }
    }
    println(ans)
}
fun dfs (visited:Array<BooleanArray>,grid:Array<CharArray>,nowy:Int,nowx:Int,ans:Int,h:Int,w:Int):Boolean{
    visited[nowy][nowx] = true
    var check = true
    if(nowx == 1 || nowx == w || nowy == 1 || nowy == h)check = false

    if(!visited[nowy+1][nowx] && grid[nowy+1][nowx] == '.' ) {
        check = dfs(visited, grid, nowy + 1, nowx, ans + 1,h,w)&&check
    }

    if(!visited[nowy-1][nowx]&& grid[nowy-1][nowx] == '.' ){
        check = dfs(visited,grid,nowy-1,nowx,ans+1,h,w)&&check

    }
    if(!visited[nowy][nowx+1]&& grid[nowy][nowx+1] == '.'){
       check = dfs(visited,grid,nowy,nowx+1,ans+1,h,w)&&check
    }
    if(!visited[nowy][nowx-1]&& grid[nowy][nowx-1] == '.'){
        check = dfs(visited,grid,nowy,nowx-1,ans+1,h,w)&&check
    }

    return check
}



@Suppress("unused")
private fun chmin(a: LongArray, idx: Int, v: Long): Boolean {
    if (v < a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

@Suppress("unused")
private fun chmax(a: LongArray, idx: Int, v: Long): Boolean {
    if (v > a[idx]) {
        a[idx] = v
        return true
    }
    return false
}

