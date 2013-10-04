// [!!!] Please change di and fn

di = "/Users/janeriongcol/Documents/CS/CS 198/ndsg-chupacabra/Graph Results/"
fn = "data_gcp2p_Utilization.txt"

//di = input("Please give directory address: ");

while(%t)
    fn = input("Please give data filename: ")
    
    if (fn == "") then
        disp("Stopping execution.");
        break;
    end
    
    // Read and open the data file
    fd = mopen(di+fn,'r')
    res = mgetl(fd, 3)
    
    // Get graph legends
    graph_title = res(1)
    x_title = res(2)
    y_title = res(3)
    
    // Get x and y values
    data = fscanfMat(di+fn)
    x_arr = data(1:$,1)
    y_arr = data(1:$,2)
    
    //plot(x_arr, y_arr, 'xr')
    plot(x_arr, y_arr, '--')
    xtitle(graph_title, x_title, y_title)
    
    mclose('all')

end
