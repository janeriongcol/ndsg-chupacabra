funcprot(0)
warning('off')

di ="C:\Users\Fatima De Villa\Desktop\Average Results\"

trad_fn = "Traditional\data_traditional_"
tsis_fn = "GCP2P\data_gcp2p_"
orange_fn = "Orange\data_orange_"

fn_arr = ["AverageLeechers.txt", "AverageNetwork.txt"]

dir_arr = [trad_fn, tsis_fn, orange_fn]

prot_arr = ["Traditional", "GCP2P", "Orange"]

index = ["1","2","3"]
disp("Network Graph")
disp([index' prot_arr'], "Please choose among the following:")



while(%t)
    choice = input("Please input index number corresponding to the protocol: ")
    choice = evstr(choice)
    
     if (choice <> 1 & choice <> 2 & choice <> 3) then
        disp("Stopping execution.")
        break
    end   
    
    clf(); 
    
    graph_title = "Network: " + string(prot_arr(choice))
    x_title = "Times (seconds)"
    y_title = "Peers"
    xtitle(graph_title, x_title, y_title)
    
    data_leechers = fscanfMat(di+dir_arr(choice)+fn_arr(1))
    x_arr = data_leechers(1:$,1)
    y_arr = data_leechers(1:$,2)
    plot(x_arr, y_arr, 'g')
    
    data_totalpeers = fscanfMat(di+dir_arr(choice)+fn_arr(2))
    x_arr = data_totalpeers(1:$,1)
    y_arr = data_totalpeers(1:$,2)
    plot(x_arr, y_arr, 'b')
    
    h1=legend(["Leechers";"Total Network Size"])
end

mclose('all')
