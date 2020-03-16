package za.botneil.bungeecore;


import java.util.concurrent.atomic.AtomicInteger;

public class Pinger implements Runnable {
  @Override
  public void run() {
    MatchME.groupMap.values().forEach((x)->{
      x.keySet().forEach(y->{
        try {
          y.update();
        } catch (Exception e) {
          y.setClosed();
        }
        if (y.getStatus() == null){
          y.setClosed();
        }else{
          AtomicInteger n = new AtomicInteger();
          while(n.get() ==0){
            MatchME.motd.forEach((f)->{
              if (y.getStatus().equals(f)){
                if(y.getOnline()<=y.getmaxPlayers()-1){
                  y.setOpen();
                }else y.setClosed();
                n.getAndIncrement();
              }
            });
          }
          //if (y.getStatus().equals("WAITING_FOR_PLAYERS")||y.getStatus().equals("WAITING")||y.getStatus().equals("STARTING")||y.getStatus().equals("motd-lobby")){
          //  if(y.getOnline()<=y.getmaxPlayers()-1){
           //   y.setOpen();
          //  }else y.setClosed();
          //} else y.setClosed();
        }

      });
    });
}
}