package za.botneil.bungeecore;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Pinger implements Runnable {
  private MatchME me;
  public Pinger(MatchME me) {this.me = me;}
  @Override
  public void run() {
    MatchME.groupMap.values().forEach((x)->{
      //todo somewhere around||make y asynchronus maybe for ultra performance
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
            AtomicReference<Boolean> loop = new AtomicReference<>(true);
            MatchME.motd.forEach((f)->{
              if(loop.get()){
                if (y.getStatus().equals(f)){
                  if(y.getOnline()<=y.getmaxPlayers()-1){
                    y.setOpen();
                    n.getAndIncrement();
                    loop.set(false);
                  }else {y.setClosed();
                    n.getAndIncrement();}
                }else{y.setClosed();
                  n.getAndIncrement();}
              }

            });
          }
        }

      });
    });
}
}