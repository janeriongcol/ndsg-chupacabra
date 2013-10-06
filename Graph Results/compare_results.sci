di ="C:\Users\LaBryan\Documents\GitHub\ndsg-chupacabra"

trad_fn = "\Traditional P2P-CDN\data_traditional_"
tsis_fn = "\tsis\data_gcp2p_"

fn_arr = ["Utilization.txt", "ConnectionSetUpTime.txt", "PlaybackDelayTime.txt"]
index = ["1","2","3"]
disp([index' fn_arr'], "Please choose among the following:")

choice = input("Please input index number corresponding to the file: ")
choice = evstr(choice)

if (choice <> 1 & choice <> 2 & choice <> 3) then
    disp("Stopping execution.")
    break
end

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

h1=legend(["Traditional";"Orange"])



