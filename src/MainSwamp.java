import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.Area;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.*;
import java.util.concurrent.TimeUnit;

@ScriptManifest(name = "Swamp Killer", author = "dokato", version = 1.0, info = "", logo = "")
public class MainSwamp extends Script{
	
	private static final Area FROG_AREA = new Area(3228,3195,3186,3167);
	  
	private HashSet<NPC> monsterSet;
	private long timeBegan;
	private long timeRan;
	private long timeBotted;
	private long timeOffline;
	private String status;
	
	public void onStart(){
		this.timeBegan = System.currentTimeMillis();
		this.timeBotted = 0;
		this.timeOffline = 0;
		monsterSet = new HashSet<>();
	}
	
	public int onLoop() throws InterruptedException{
		status="loop started";
		if(getClient().isLoggedIn()){
			procedures();
			if(FROG_AREA.contains(myPlayer())){
				fight();
	        }else{
	        	walkToSwamp();
	        }
		}
		status="loop ended";
		return 0;
	}
	
	public void onPaint(Graphics2D g1){
		this.timeRan = (System.currentTimeMillis() - this.timeBegan);
		if (getClient().isLoggedIn()) {
			this.timeBotted = (this.timeRan - this.timeOffline);
		} else {
			this.timeOffline = (this.timeRan - this.timeBotted);
		}
		
		Graphics2D g = g1;
		
		g.setFont(new Font("Arial", 0, 13));
		g.setColor(new Color(255, 255, 255));
		g.drawString("Version: " + getVersion(), 20, 50);
		g.drawString("Runtime: " + ft(this.timeRan), 20, 65);
		g.drawString("Time botted: " + ft(this.timeBotted), 20, 80);
		g.drawString("Status: " + this.status, 20, 95);
		
		g.drawString("Attk: " + getSkills().getStatic(Skill.ATTACK), 20,115 );
		g.drawString("" + getSkills().experienceToLevel(Skill.ATTACK), 20,130 );
		
	    g.drawString("Str: " + getSkills().getStatic(Skill.STRENGTH), 20, 160);
	    g.drawString("" + getSkills().experienceToLevel(Skill.STRENGTH), 20, 175);
	    
	    
	    g.drawString("Def: " + getSkills().getStatic(Skill.DEFENCE), 20, 205);
	    g.drawString("" + getSkills().experienceToLevel(Skill.DEFENCE), 20, 220);
	    
	    g.drawString("Hp" + getSkills().getStatic(Skill.HITPOINTS), 20, 250);
	    g.drawString("" + getSkills().experienceToLevel(Skill.HITPOINTS), 20, 265);
	}
	
	 private void walkToSwamp(){
	        status="Walking to swamp";
	        if(map.canReach(new Position(3197,3188,0))){
	                localWalker.walk(new Area(3194,3190,3200,3180),true);
	        }else if(map.canReach(new Position(3209,3187,0))){
	                localWalker.walk(new Area(3211,3192,3207,3187),true);
	        }else if(map.canReach(new Position(3218,3185,0))){
	                localWalker.walk(new Area(3221,3188,3217,3184),true);
	        }else if(map.canReach(new Position(3226,3187,0))){
	                localWalker.walk(new Area(3228,3189,3225,3184),true);
	        }else if(map.canReach(new Position(3239,3188,0))){
	                localWalker.walk(new Area(3244,3184,3239,3182),true);
	        }else if(map.canReach(new Position(3238,3201,0))){
	                localWalker.walk(new Area(3240,3195,3236,3200),true);
	        }else if(map.canReach(new Position(3234,3212,0))){
	                localWalker.walk(new Area(3233,3206,3236,3211),true);
	        }
	    }
	
	/*
     * giant frog 2143
     * Big frog 2144
     * frog 2145
     * rat  2864
     */
	private void fight() throws InterruptedException{
		if(((!myPlayer().isUnderAttack()&&!myPlayer().isAnimating())&&!getCombat().isFighting())&&!myPlayer().isMoving()){
			status="about to get npcList";
			List<NPC> npcsList = getNpcs().getAll();
			status="just getted npcList";
			for(NPC npc : npcsList){
				status="just entered for loop";
				int npcId = npc.getId();
				if(((npcId==2143||npcId==2144)||npcId==2145)||npcId==2864){
					if(!npc.isUnderAttack()&&npc.isAttackable()){
						status="about to add to monsterSet";
						monsterSet.add(npc);
						status="just added to monsterSet";
					}
				}
			}
			NPC monster = npcs.closest(monsterSet);
			if((!monster.isAnimating() || monster.isMoving()) && !myPlayer().isMoving()){
			status="about to attack the monster";
				monster.interact("Attack");
				sleep(random(300,600));
			}
		}
		status="about to clear the monsterSet";
		monsterSet.clear();
		status="just cleared the monsterSet";
	}
	
	private void procedures() throws InterruptedException{
		getCamera().toTop();
		if(getInventory().isItemSelected()){
			getInventory().deselectItem();
			sleep(random(200,400));
		}
		if(getSettings().getRunEnergy()<random(7,14)){
			getSettings().setRunning(true);
			sleep(random(200,400));
		}
		if(inventory.contains("Adamant scimitar")){
	            inventory.getItem("Adamant scimitar").interact("Wield");
	            sleep(random(200,400));
	    }
		if(inventory.contains("Iron platebody")){
	            inventory.getItem("Iron platebody").interact("Wear");
	            sleep(random(200,400));
	    }
	    if(inventory.contains("Iron kiteshield")){
	            inventory.getItem("Iron kiteshield").interact("Wear");
	            sleep(random(200,400));
	    }
	     
	}
	
	private String ft(long duration) {
		String res = "";
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration)
				- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(duration));
		if (days == 0L) {
			res = hours + ":" + minutes + ":" + seconds;
		} else {
			res = days + ":" + hours + ":" + minutes + ":" + seconds;
		}
		return res;
	}
}
