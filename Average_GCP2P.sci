di = pwd() + "/Documents"num_files = 15num_cycles = 100 file_names = ["data_gcp2p_AverageConnectionSetUpTime.txt", "data_gcp2p_AverageUtilization.txt", "data_gcp2p_AveragePlaybackDelayTime.txt", "data_gcp2p_AverageRTT.txt", "data_gcp2p_AverageReject.txt"]conn_arr = ["data_gcp2p_ConnectionSetUpTime1.txt", "data_gcp2p_ConnectionSetUpTime2.txt", "data_gcp2p_ConnectionSetUpTime3.txt",         "data_gcp2p_ConnectionSetUpTime4.txt", "data_gcp2p_ConnectionSetUpTime5.txt", "data_gcp2p_ConnectionSetUpTime6.txt",         "data_gcp2p_ConnectionSetUpTime7.txt", "data_gcp2p_ConnectionSetUpTime8.txt", "data_gcp2p_ConnectionSetUpTime9.txt",         "data_gcp2p_ConnectionSetUpTime10.txt", "data_gcp2p_ConnectionSetUpTime11.txt", "data_gcp2p_ConnectionSetUpTime12.txt",         "data_gcp2p_ConnectionSetUpTime13.txt", "data_gcp2p_ConnectionSetUpTime14.txt", "data_gcp2p_ConnectionSetUpTime15.txt"]util_arr = ["data_gcp2p_Utilization1.txt", "data_gcp2p_Utilization2.txt", "data_gcp2p_Utilization3.txt",         "data_gcp2p_Utilization4.txt", "data_gcp2p_Utilization5.txt", "data_gcp2p_Utilization6.txt",         "data_gcp2p_Utilization7.txt", "data_gcp2p_Utilization8.txt", "data_gcp2p_Utilization9.txt",         "data_gcp2p_Utilization10.txt", "data_gcp2p_Utilization11.txt", "data_gcp2p_Utilization12.txt",         "data_gcp2p_Utilization13.txt", "data_gcp2p_Utilization14.txt", "data_gcp2p_Utilization15.txt"]         play_arr = ["data_gcp2p_PlaybackDelayTime1.txt", "data_gcp2p_PlaybackDelayTime2.txt", "data_gcp2p_PlaybackDelayTime3.txt",         "data_gcp2p_PlaybackDelayTime4.txt", "data_gcp2p_PlaybackDelayTime5.txt", "data_gcp2p_PlaybackDelayTime6.txt",         "data_gcp2p_PlaybackDelayTime7.txt", "data_gcp2p_PlaybackDelayTime8.txt", "data_gcp2p_PlaybackDelayTime9.txt",         "data_gcp2p_PlaybackDelayTime10.txt", "data_gcp2p_PlaybackDelayTime11.txt", "data_gcp2p_PlaybackDelayTime12.txt",         "data_gcp2p_PlaybackDelayTime13.txt", "data_gcp2p_PlaybackDelayTime14.txt", "data_gcp2p_PlaybackDelayTime15.txt"]         rtt_arr = ["data_gcp2p_AverageRTT1.txt", "data_gcp2p_AverageRTT2.txt", "data_gcp2p_AverageRTT3.txt",         "data_gcp2p_AverageRTT4.txt", "data_gcp2p_AverageRTT5.txt", "data_gcp2p_AverageRTT6.txt",         "data_gcp2p_AverageRTT7.txt", "data_gcp2p_AverageRTT8.txt", "data_gcp2p_AverageRTT9.txt",         "data_gcp2p_AverageRTT10.txt", "data_gcp2p_AverageRTT11.txt", "data_gcp2p_AverageRTT12.txt",         "data_gcp2p_AverageRTT13.txt", "data_gcp2p_AverageRTT14.txt", "data_gcp2p_AverageRTT15.txt"]         rej_arr = ["data_gcp2p_AverageReject1.txt", "data_gcp2p_AverageReject2.txt", "data_gcp2p_AverageReject3.txt",         "data_gcp2p_AverageReject4.txt", "data_gcp2p_AverageReject5.txt", "data_gcp2p_AverageReject6.txt",         "data_gcp2p_AverageReject7.txt", "data_gcp2p_AverageReject8.txt", "data_gcp2p_AverageReject9.txt",         "data_gcp2p_AverageReject10.txt", "data_gcp2p_AverageReject11.txt", "data_gcp2p_AverageReject12.txt",         "data_gcp2p_AverageReject13.txt", "data_gcp2p_AverageReject14.txt", "data_gcp2p_AverageReject15.txt"]function compute(arr, name, ttl, x, y)    ave = zeros(num_files,2)    tot = zeros(num_cycles,1)        leg = [ttl; x; y]    disp(leg)        for i=1:num_cycles        ave(i,1) = i-1    end        for i=1:num_cycles        for j=1:num_files            fd = mopen(di+arr(j),'r')            res = fscanfMat(di+arr(j))            tot(i) = tot(i) + res(i,2)            mclose(fd)        end        ave(i,2) = tot(i)/num_files    end    //ave = string([leg ;ave])    //disp(ave)    //write(di+file_names(name),ave)    fprintfMat(di+file_names(name), ave)endfunctioncompute(conn_arr, 1, "Average Connection Set-up Time", "Time", "Connection Set-up Time")compute(util_arr, 2, "Average Utilization Rate", "Time", "Utilization (%)")compute(play_arr, 3, "Average Playback Delay Time", "Time", "Playback Delay Time")compute(rtt_arr, 4, "Average RTT", "Time", "RTT")compute(rej_arr, 5, "Average Rejection Rate", "Time", "Rejection (%)")mclose('all')