funcprot(0)
warning('off')

di ="C:\Users\Fatima De Villa\Desktop\"

trad_fn = "Traditional P2P-CDN\data_traditional_"
tsis_fn = "tsis\data_gcp2p_"
orange_fn = "Orange\data_orange_"

fn_arr = ["Utilization.txt", "ConnectionSetUpTime.txt", "PlaybackDelayTime.txt", "AverageReject.txt"]
index = ["1","2","3","4"]
disp([index' fn_arr'], "Please choose among the following:")



while(%t)
    choice = input("Please input index number corresponding to the file: ")
    choice = evstr(choice)
    
    if (choice <> 1 & choice <> 2 & choice <> 3 & choice <> 4) then
        disp("Stopping execution.")
        break
    end   
    
    clf(); 

    fd = mopen(di+trad_fn+fn_arr(choice),'r')
    res = mgetl(fd, 3)
    
    graph_title = res(1)
    x_title = res(2)
    y_title = res(3)
    xtitle(graph_title, x_title, y_title)
    
    data_trad = fscanfMat(di+trad_fn+fn_arr(choice))
    x_arr = data_trad(1:$,1)
    y_arr = data_trad(1:$,2)
    plot(x_arr, y_arr, 'r')
    
    data_tsis = fscanfMat(di+tsis_fn+fn_arr(choice))
    x_arr = data_tsis(1:$,1)
    y_arr = data_tsis(1:$,2)
    plot(x_arr, y_arr, 'b')
    
    data_tsis = fscanfMat(di+orange_fn+fn_arr(choice))
    x_arr = data_tsis(1:$,1)
    y_arr = data_tsis(1:$,2)
    plot(x_arr, y_arr, 'g')
    
    h1=legend(["Traditional Adapted";"GCP2P Adapted";"Orange"])
end

mclose('all')



