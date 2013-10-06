// [!!!] Please change di accordingly

di = pwd() + "/Documents/CS/CS 198/ndsg-chupacabra/tsis/"

//di = input("Please give directory address: ");

//while(%t)
    clf()
    
    fn_arr = ["data_gcp2p_Utilization.txt", "data_gcp2p_ConnectionSetUpTime.txt", "data_gcp2p_PlaybackDelayTime.txt"]
    index = ["1","2","3"]
    disp([index' fn_arr'], "Please choose among the following:")
    
    choice = input("Please input index number corresponding to the file: ")
    choice = evstr(choice)
    
    if (choice <> 1 & choice <> 2 & choice <> 3) then
        disp("Stopping execution.");
        break;
    end
    
    // Read and open the data file
    fd = mopen(di+fn_arr(choice),'r')
    res = mgetl(fd, 3)
    
    // Get graph legends
    graph_title = res(1)
    x_title = res(2)
    y_title = res(3)
    
    // Get x and y values
    data = fscanfMat(di+fn_arr(choice))
    x_arr = data(1:$,1)
    y_arr = data(1:$,2)
    
    //plot(x_arr, y_arr, 'xr')
    plot(x_arr, y_arr, '--')
    xtitle(graph_title, x_title, y_title)
    
    mclose('all')

//end
