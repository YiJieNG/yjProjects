/**
 * FIT2102 Assignment 1 
 * 31158145 YI JIE NG
 * Project are built out with references of Asteroid Game FRP
 */


/**
 * Import for observable method needed from rxjs library 
 */
import { interval, fromEvent, zip} from 'rxjs'
import { map, scan, filter, flatMap, takeUntil, merge, reduce, subscribeOn} from 'rxjs/operators'

function spaceinvaders() {
  // Inside this function you will use the classes and functions 
  // from rx.js
  // to add visuals to the svg element in pong.html, animate them, and make them interactive.
  // Study and complete the tasks in observable exampels first to get ideas.
  // Course Notes showing Asteroids in FRP: https://tgdwyer.github.io/asteroids/ 
  // You will be marked on your functional programming style
  // as well as the functionality that you implement.
  // Document your code!  

  /**
   * Class
   */
  class Tick {constructor(public readonly elapsed: number){}} 
  class Move {constructor(public readonly lrDirection: number){}}
  class Shoot { constructor() {} }
  
  /**
   * type
   */
  type Event = 'keydown' | 'keyup'  // Keyboard event type
  type Key = 'ArrowLeft' | 'ArrowRight' | 'ArrowDown' | 'Space' // Key type
  type ViewType = 'ship' | 'alien' | 'bullet' | 'shield' | 'aliensBullets' | 'score' | 'phase' | 'baseLine' | 'gameWin' | 'restart' // View type
  type Body = Readonly<{
    id: String,
    x: number,
    y: number,
    xDirection: number,
    yDirection: number,
    radius: number,
    createTime: number,
  }> // Body type
  // Modification and adaption of Body in Asteroid Game
  type State = Readonly<{
    time: number,
    ship: Body,
    bullets: ReadonlyArray<Body>,
    aliensBullets: ReadonlyArray<Body>,
    aliens: ReadonlyArray<Body>,
    exit: ReadonlyArray<Body>,
    shield: ReadonlyArray<Body>,
    objCount: number,
    bonusMark: number,
    gameOver: Boolean,
    gameWin: Boolean,
    score: number,
    stage: number,
    highScore: number,
    victory: Boolean
  }> // State type
  // Modification and adaption of State type in Asteroid Game

  // Boundary of width for the canvas 
  const xLimit = Number(document.getElementById("canvas")!.getAttribute("width"));
  
  // Immutable variable (Act like control center)
  // Modification and adaption of Constant in Asteroid Game
  const 
    Constant = {
      CANVAS_SIZE: 600,
      BULLET_EXPIRED_TIME: 500,
      BULLET_RADIUS: 3,
      ALIEN_RADIUS: 10,
      ALIEN_COLUMNS: 5,
      ALIENS_ROWS: 8,
      ALIEN_PHASE1_BULLET_RATE: 120,
      ALIEN_PHASE2_BULLET_RATE: 80,
      ALIEN_PHASE3_BULLET_RATE: 65,
      ALIEN_NORMAL_BULLET_SPEED: 1.5,
      ALIEN_RAPID_BULLET_SPEED: 3,
      SHIP_PHASE1_BULLET_SPEED: 1,
      SHIP_PHASE2_BULLET_SPEED: 3,
      SHIP_PHASE3_BULLET_SPEED: 5,
      DEFAULT_QUANTITY: 1,
      SHIP_BULLETS_INTERVAL: 10,
      ALIEN_BULLETS_INTERVAL: 15,
      SHIELD_X: 50,
      SHIELD_Y: 350,
      SHIELD_RADIUS: 5,
      SHIELD_INTERVAL: 100,
      FINAL_STAGE: 5
    } as const
  
  /**
   * Fuction which creates bullet for ship
   * @param s State, current state condition
   * @param x number, interval for the bullet if it is necessary (used in createdoubleBullet and createTripleBullet) 
   * @param speed number, bullet speed
   * @param quantity number, number of bullets to create in one shot
   * @returns Body, bullet shot from ship
   */
  function createBullet(s:State, x:number, speed:number, quantity:number): Body{
    return {
      id: quantity+`bullet${s.objCount}`,
      x: s.ship.x+x,
      y: s.ship.y-10,
      xDirection: 0,
      yDirection: -1*speed, 
      createTime: s.time,
      radius: Constant.BULLET_RADIUS
    }
  }

  /**
   * Fuction which creates two bullets in one shot for ship
   * @param s State, current state condition
   * @param speed number, bullet speed
   * @returns ReadonlyArray<Body>, Array of bullet created
   */
  function createDoubleBullet(s:State, speed: number): ReadonlyArray<Body>{
    return [createBullet(s,-1*Constant.SHIP_BULLETS_INTERVAL,speed,Constant.DEFAULT_QUANTITY)]
      .concat([createBullet(s,Constant.SHIP_BULLETS_INTERVAL,speed,Constant.DEFAULT_QUANTITY+1)])
  }

  /**
   * Fuction which creates three bullets in one shot for ship
   * @param s State, current state condition
   * @param speed number, bullet speed
   * @returns ReadonlyArray<Body>, Array of bullet created
   */
  function createTripleBullet(s:State, speed: number): ReadonlyArray<Body>{
    return [createBullet(s,-10,speed,Constant.DEFAULT_QUANTITY)]
      .concat([createBullet(s,10,speed,Constant.DEFAULT_QUANTITY+1)])
      .concat([createBullet(s,0,speed,Constant.DEFAULT_QUANTITY+2)])
  }

  /**
   * Fuction which creates bullet for aliens
   * @param s State, current state condition
   * @param o Body, alien who make a shoot
   * @param x number, interval for the bullet if it is necessary (used in createEnemyRapidBullet) 
   * @param speed number, bullet speed
   * @param quantity number, number of bullets to create in one shot
   * @returns Body, Bullet of the alien
   */
  function createEnemyBullet(s:State, o:Body, x:number, speed:number, quantity:number): Body{
    return {
      id: quantity+`bullet${s.objCount}`,
      x: o.x+x,
      y: o.y,
      xDirection: 0,
      yDirection: 1*speed, 
      createTime: s.time,
      radius: Constant.BULLET_RADIUS
    }
  }

  /**
   * Fuction which creates 5 bullets in one shot for aliens
   * @param s State, current state condition
   * @param o Body, alien who make a shoot
   * @param speed number, bullet speed
   * @returns ReadonlyArray<Body>, 5 bullets created in one shot by alien
   */
  function createEnemyRapidBullet(s:State, o:Body, speed: number): ReadonlyArray<Body>{
    return [createEnemyBullet(s,o,0,speed,Constant.DEFAULT_QUANTITY)]
      .concat([createEnemyBullet(s,o,Constant.ALIEN_BULLETS_INTERVAL,speed,Constant.DEFAULT_QUANTITY+1)])
      .concat([createEnemyBullet(s,o,-1*Constant.ALIEN_BULLETS_INTERVAL,speed,Constant.DEFAULT_QUANTITY+2)])
      .concat([createEnemyBullet(s,o,2*Constant.ALIEN_BULLETS_INTERVAL,speed,Constant.DEFAULT_QUANTITY+3)])
      .concat([createEnemyBullet(s,o,-2*Constant.ALIEN_BULLETS_INTERVAL,speed,Constant.DEFAULT_QUANTITY+4)])
  }

  /**
   * Function which creates a ship
   * @returns Body, ship object
   */
  // Modification and adaption of createShip() in Asteroid Game
  function createShip(): Body{
    return {
      id: 'ship',
      x: 300,
      y: 550,
      xDirection: 0,
      yDirection: 0,
      createTime: 0,
      radius: 10
    }
  }

  /**
   * Constant which determine the shield creation way
   * @returns Body shield object
   */
  const createShield = (viewType: ViewType)=>(oid: number) => (time:number)=> (radius:number)=> (x: number)=> (y: number)=>
    <Body>{
      id: viewType+oid+x+y,
      x: x,
      y: y+(oid*10),
      xDirection: 0,
      yDirection: 0,
      createTime: time,
      radius: radius,
      viewType: viewType,
    }
  
  /**
   * Function which create shield in a row
   * @param r number, row of shield
   * @param x number, x of shield
   * @param y number, y of shield
   * @returns ReadonlyArray<Body>, Array of shield in a row
   */
  function startShieldRows(r:number, x: number, y: number): ReadonlyArray<Body>{
    return r==1?startShield(1,x,y): startShield(r,x,y).concat(startShieldRows(r-1,x,y))
  }

  /**
   * Function which create shield in a group
   * @param r number, group of shield
   * @param x number, x of shield
   * @param y number, y of shield
   * @returns ReadonlyArray<Body>, Array of shield in a group
   */
  function startShield(r: number, x: number, y: number) :ReadonlyArray<Body>{ 
    return [...Array(3)]
    .map((_,i)=> createShield('shield')(i)(0)(10)(r*10+x)(y))
  }

  /**
   * Constant which determine createAlien method
   * @returns <Body>, alien object
   */
  // Modification and adaption of createAlien in Asteroid Game
  const createAlien = (viewType: ViewType)=>(oid: number) => (time:number)=> (radius:number)=> (x: number)=> (y: number)=>
    <Body>{
      id: viewType+oid+x,
      x: x,
      y: y+(oid*30),
      xDirection: 0,
      yDirection: 0,
      createTime: time,
      radius: radius,
      viewType: viewType,
    }

  /**
   * Constant which create aliens row by row
   * @param r number, row of aliens
   * @returns ReadonlyArray<Body>, Row of aliens
   */
  const startAliensRows=(r:number)=>{
    return r==1?startAlien(1): startAlien(r).concat(startAliensRows(r-1))
  }

  /**
   * Constant which create alien rows column by column
   * @param r number, column of aliens
   * @returns ReadonlyArray<Body>, Column of aliens
   */
  const startAlien= (r: number) =>{ 
    return [...Array(Constant.ALIEN_COLUMNS)]
    .map((_,i)=> createAlien('alien')(i)(0)(Constant.ALIEN_RADIUS)(r*60)(50))
  }

  /**
   * Constant which determine initialState
   */
  // Modification and adaption of INITIAL_STATE in Asteroid Game
  const INITIAL_STATE: State = {
    time: 0,
    ship: createShip(),
    bullets: [],
    aliensBullets: [],
    aliens: startAliensRows(Constant.ALIENS_ROWS),
    exit: [],
    objCount: 0,
    shield: startShieldRows(Constant.SHIELD_RADIUS, Constant.SHIELD_X, Constant.SHIELD_Y).
      concat(startShieldRows(Constant.SHIELD_RADIUS, Constant.SHIELD_X+Constant.SHIELD_INTERVAL, Constant.SHIELD_Y)).
      concat(startShieldRows(Constant.SHIELD_RADIUS, Constant.SHIELD_X+2*Constant.SHIELD_INTERVAL, Constant.SHIELD_Y)).
      concat(startShieldRows(Constant.SHIELD_RADIUS, Constant.SHIELD_X+3*Constant.SHIELD_INTERVAL, Constant.SHIELD_Y)).
      concat(startShieldRows(Constant.SHIELD_RADIUS, Constant.SHIELD_X+4*Constant.SHIELD_INTERVAL, Constant.SHIELD_Y)),
    bonusMark: 0,
    gameOver: false,
    gameWin: false, 
    score: 0,
    stage: 1,
    highScore: 0,
    victory: false
  }
  

  // Observable stream for keyboard event
  // Modification and adaption of ObserveKey in Asteroid Game
  const ObserveKey = <T>(e:Event, k:Key, result:()=>T)=>
  fromEvent<KeyboardEvent>(document,e).pipe(
    filter(({code})=>code === k),
    filter(({repeat})=>!repeat),
    map(result)
  )
  // Movement available for the ship 
  const moveLeft = ObserveKey('keydown', 'ArrowLeft', ()=> new Move(-1)),
  moveRight = ObserveKey('keydown', 'ArrowRight', ()=> new Move(1)),
  // stopLeft = observeKey('keyup', 'ArrowLeft', ()=> new Move(0)),
  // stopRight = observeKey('keyup', 'ArrowRight', ()=> new Move(0)),
  stopMove = ObserveKey('keydown', 'ArrowDown', ()=> new Move(0)),
  shoot = ObserveKey('keydown','Space', ()=>new Shoot())

  /**
   * Function which handle collision of two different objects
   * @param s State, current state
   * @returns State, state after handled collisions
   */
  // Modification and adaption of handleCollisions in Asteroid Game
  function handleCollisions (s:State): State {
    // determine the bodies' shape collided, filter all the collided object out, flatmap them and add them to exit array to finish collision action
    const 
      not = <T>(f:(x:T)=>boolean)=> (x:T)=> !f(x),
      bodiesCollided = ([a,b]:[Body,Body])=>(a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y)<=(a.radius+b.radius)*(a.radius+b.radius),
      circleRectangleCollided = ([a,b]:[Body,Body])=>(a.x-Math.max(b.x, Math.min(a.x, b.x+b.radius)))*(a.x-Math.max(b.x, Math.min(a.x, b.x+b.radius)))+(a.y-Math.max(b.y, Math.min(a.y, b.y+b.radius)))*(a.y-Math.max(b.y, Math.min(a.y, b.y+b.radius)))<=(a.radius)*(a.radius),
      
      // ship collision
      shipCollided = s.aliensBullets.filter(a=>bodiesCollided([s.ship,a])).length > 0, 
      // bullets and aliens collision
      allBulletsAndAliens = s.bullets.flatMap(b=> s.aliens.map(a=>([b,a]))),
      collidedBulletsAndAliens = allBulletsAndAliens.filter(bodiesCollided),
      collidedBullets = collidedBulletsAndAliens.map(([bullet,_])=>bullet),
      collidedAliens = collidedBulletsAndAliens.map(([_,alien])=>alien),
      // bullets and shield collision
      allEnemyBulletsAndShield = s.aliensBullets.flatMap(b=> s.shield.map(a=>([b,a]))),
      collidedEnemyBulletsAndShield = allEnemyBulletsAndShield.filter(circleRectangleCollided),
      collidedEnemyBullets = collidedEnemyBulletsAndShield.map(([bullet,_])=>bullet),
      collidedShield = collidedEnemyBulletsAndShield.map(([_,shield])=>shield),
      allBulletsAndShield = s.bullets.flatMap(b=> s.shield.map(a=>([b,a]))),
      collidedBulletsAndShield = allBulletsAndShield.filter(circleRectangleCollided),
      collidedMyBullets = collidedBulletsAndShield.map(([bullet,_])=>bullet),
      collidedMyShield = collidedBulletsAndShield.map(([_,shield])=>shield),
      // bullets and bullets collision
      allBulletsAndEnemyBullets = s.bullets.flatMap(b=> s.aliensBullets.map(a=>([b,a]))),
      collidedBulletsAndEnemyBullets = allBulletsAndEnemyBullets.filter(bodiesCollided),
      collidedMYBullets = collidedBulletsAndEnemyBullets.map(([bullet,_])=>bullet),
      collidedALBullets = collidedBulletsAndEnemyBullets.map(([_,eBullet])=>eBullet),
      //alien and shield collision
      allAliensAndShield = s.aliens.flatMap(b=> s.shield.map(a=>([b,a]))),
      collidedAliensAndShield = allAliensAndShield.filter(circleRectangleCollided),
      collidedShieldS = collidedAliensAndShield.map(([_,shield])=>shield),
      // determine the element
      elem = (a:ReadonlyArray<Body>) => (e:Body) => a.findIndex(b=>b.id === e.id) >= 0,
      // all the elements except...
      except = (a:ReadonlyArray<Body>) => (b:Body[]) => a.filter(not(elem(b)))
    return <State>{
      ...s,
      bullets: except(s.bullets)(collidedBullets.concat(collidedMyBullets,collidedMYBullets)),
      aliens: except(s.aliens)(collidedAliens),
      aliensBullets: except(s.aliensBullets)(collidedEnemyBullets.concat(collidedALBullets)),
      shield: except(s.shield)(collidedShield.concat(collidedMyShield, collidedShieldS)),
      exit: s.exit.concat(collidedBullets,collidedAliens,collidedEnemyBullets,collidedShield,collidedMyShield,collidedMyBullets,collidedMYBullets,collidedALBullets,collidedShieldS),
      gameOver: s.gameOver||shipCollided,
      score: s.score+collidedAliens.length,
      highScore: s.score+collidedAliens.length > s.highScore?s.score+collidedAliens.length:s.highScore,
      victory: s.stage == Constant.FINAL_STAGE && s.gameWin
    }
  }
  
  /**
   * Function which takes state as input to update the view of html according to the current state
   * @param state State current state
   */
  // Modification and adaption of updateView in Asteroid Game
  function updateView(state: State){
    const svg = document.getElementById("canvas")!,
    ship = document.getElementById("ship")!;
    ship.setAttribute('transform', `translate(${state.ship.x},${state.ship.y})`);

    /**
     * Function which create Text svg element
     * @param viewType ViewType of the text element
     * @param id String id of the element
     * @param x x coordinate of text
     * @param y x coordinate of text
     * @returns Element text
     */
    function createText (viewType: ViewType, id:string, x: number, y: number){
      const v = document.createElementNS(svg.namespaceURI, 'text');
      v.setAttribute("id",id);
      v.classList.add(viewType)
      v.setAttribute("x",String(x))
      v.setAttribute("y",String(y))
      svg.appendChild(v) 
      return v;
    } 

    /**
     * Function which create rectangle element view
     * @param viewType ViewType of the rectangle element
     * @param id String id of the element
     * @returns Element rectangle
     */
    function createRect(viewType: ViewType, id:string){
      const y = document.createElementNS(svg.namespaceURI, 'rect');
      y.setAttribute("id",id);
      y.classList.add(viewType)
      svg.appendChild(y) 
      return y;
    }

    /**
     * Function which create ellipse element view
     * @param viewType ViewType of the ellipse element
     * @param id String id of the element
     * @returns Element ellipse
     */
    function createEllipse(viewType: ViewType, id:string){
      const y = document.createElementNS(svg.namespaceURI, 'ellipse');
      y.setAttribute("id",id);
      y.classList.add(viewType)
      svg.appendChild(y) 
      return y;
    }

    /**
     * Constant which create all the views needed
     */
    const v = document.getElementById("ScoreText") || createText("score","ScoreText",10,30);
    v.textContent = "Score: " + state.score;
    const x = document.getElementById("PhaseText") || createText("phase","PhaseText",450,30);
    x.textContent = "STAGE " + state.stage + " PHASE " + (state.aliens.length>15?1:state.aliens.length>1?2:3);
    const z = document.getElementById("HighScoreText") || createText("score","HighScoreText",250,30);
    z.textContent = "High Score: " + state.highScore;

    const y = document.getElementById("baseLine") || createRect("baseLine", "baseLine");
    setRectAttribute(y, 0, 510, 600, 5)

    // create all of the current shields view
    state.shield.forEach(b=>{
      const v = document.getElementById(String(b.id)) || createRect("shield", String(b.id));
      setRectAttribute(v, b.x, b.y, 10, 10)
    })
    // create all of the current bullets view
    state.bullets.forEach(b=>{
      const v = document.getElementById(String(b.id)) || createEllipse("bullet", String(b.id));
      setMyAttribute(v, b.x, b.y, b.radius, b.radius)
    })
    // create all of the current aliensBullets view
    state.aliensBullets.forEach(b=>{
      const v = document.getElementById(String(b.id)) || createEllipse("aliensBullets", String(b.id));
      setMyAttribute(v, b.x, b.y, b.radius, b.radius)
    })
    // create all of the current aliens view
    state.aliens.forEach(b=>{
      const v = document.getElementById(String(b.id)) || createEllipse("alien", String(b.id));
      setMyAttribute(v, b.x, b.y, b.radius, b.radius)
    })
    // if pass all the 5 stages and their 3 phases, show win message and let user restart
    state.victory?restart():null
    // set x,y, radius x and y attributes of ellipse
    function setMyAttribute(v: Element, cx: number, cy: number, rx: number, ry: number){
      v.setAttribute("cx",String(cx))
      v.setAttribute("cy",String(cy))
      v.setAttribute("rx",String(rx)) 
      v.setAttribute("ry",String(ry))
    }
    // set x,y, width and height attributes of ellipse
    function setRectAttribute(v: Element, cx: number, cy: number, width: number, height: number){
      v.setAttribute("x",String(cx))
      v.setAttribute("y",String(cy))
      v.setAttribute("width",String(width)) 
      v.setAttribute("height",String(height))
    }
    // restart function which make user available to restart the game if the pass all the stages and phases
    function restart(){
      subscription.unsubscribe();
      const v = document.getElementById("gameWin") || createText("gameWin","gameWin",120,300);
      v.textContent = "Congratulation! You win the game! Final Score: " + state.highScore;
      const c = document.getElementById("restart") || createText("gameWin","restart",160,350);
      c.textContent = "Press ENTER to restart the game";
      const key = fromEvent<KeyboardEvent>(document, "keydown")
      key.pipe(filter(({code}) => code == "Enter")).subscribe(_=>window.location.reload())
    }
    // remove all the expired or unneeded element of UI
    state.exit.forEach(o=>{
      const v = document.getElementById(String(o.id));
      v?svg.removeChild(v):null
    })
  }
  // Constant which helps to move the element
  const moveObj=(o: Body)=> <Body>{
    ...o,
    x: (o.x + o.xDirection),
    y: (o.y + o.yDirection)
  }
  // Constant which helps to move the alien with constant speed
  const moveAliens=(s:State)=>(o:Body)=> <Body>{
    ...o,
    createTime: o.createTime == 1200?0:o.createTime + 2,
    x: (o.x + o.xDirection),
    y: (o.y + o.yDirection),      
    xDirection: o.createTime > 100 && o.createTime < 500 && o.createTime%10==0? 1: o.createTime > 700 && o.createTime < 1100&& o.createTime%10==0? -1 : 0,
    yDirection: o.y ==600? 0: o.createTime % 600 == 0&&o.createTime != 0? (s.stage*5):0
  }
  // Constant which helps to move the alien rapidly
  const moveAliensRapid=(o:Body)=> <Body>{
    ...o,
    x: o.x ==550?o.x-2:o.x ==50?o.x+2:o.x%2==0?(o.x + o.xDirection):o.x+1,
    y: (o.y + o.yDirection),
    xDirection: Math.abs(o.xDirection)!=2?2:(o.x ==50||o.x ==550)?(-1)*o.xDirection:o.xDirection,
    yDirection: (o.x ==50||o.x ==550)?20:0
  }
  // Constant which finalise and return the state of the game during every stream of the subscription
  // Modification and adaption of tick in Asteroid Game
  const tick = (s:State,elapsed:number) => {
    const not = <T>(f:(x:T)=>boolean)=>(x:T)=>!f(x),
      expired = (b:Body)=>(elapsed - b.createTime) > Constant.BULLET_EXPIRED_TIME,
      expiredEnemy = (b:Body)=>(elapsed - b.createTime) > Constant.BULLET_EXPIRED_TIME,
      expiredBullets:Body[] = s.bullets.filter(expired),
      activeBullets = s.bullets.filter(not(expired)),
      activeEnemyBullets = s.aliensBullets.filter(not(expiredEnemy)),
      expiredEnemyBullets:Body[] = s.aliensBullets.filter(expiredEnemy);
      s = handleCollisions(<State>{...s, 
        ship: s.ship.x+s.ship.xDirection > xLimit-50 || s.ship.x+s.ship.xDirection < 50?{...s.ship}:moveObj(s.ship), // make sure the ship is within the boundary
        bullets:activeBullets.map(moveObj),  // move all the active bullets
        aliens:s.aliens.length>1?s.aliens.map(moveAliens(s)):s.aliens.map(moveAliensRapid), // move the aliens according to phase and stage
        aliensBullets: // create aliens bullets base on stage and phase
          s.aliens.length==0?[]:s.aliens.length>15? 
          s.time%(Constant.ALIEN_PHASE1_BULLET_RATE-(s.stage*10))==0?activeEnemyBullets.concat(createEnemyBullet(s,s.aliens[Math.floor(Math.random()*s.aliens.length)], 0, Constant.ALIEN_NORMAL_BULLET_SPEED, Constant.DEFAULT_QUANTITY)).map(moveObj):s.aliensBullets.map(moveObj):
          s.aliens.length!=1?
          s.time%(Constant.ALIEN_PHASE2_BULLET_RATE-(s.stage*10))==0?activeEnemyBullets.concat(createEnemyBullet(s,s.aliens[Math.floor(Math.random()*s.aliens.length)], 0, Constant.ALIEN_NORMAL_BULLET_SPEED, Constant.DEFAULT_QUANTITY)).map(moveObj):s.aliensBullets.map(moveObj):
          s.aliens.length==1?
          s.time%(Constant.ALIEN_PHASE3_BULLET_RATE-(s.stage*10))==0?activeEnemyBullets.concat(createEnemyRapidBullet(s,s.aliens[0],Constant.ALIEN_RAPID_BULLET_SPEED)):s.aliensBullets.map(moveObj):s.aliensBullets.map(moveObj),
        exit:s.aliens.length==0?expiredBullets.concat(s.aliensBullets):expiredBullets.concat(expiredEnemyBullets), // handle all the collied or expired elements
        time:elapsed,
        objCount: s.objCount + 1,
        gameOver: s.aliens.filter(b=>b.y >= 500).length>=1, // when the aliens reach the base line we will then lose the game
        gameWin: s.aliens.length==0?true:false
      })
      // handle the stage and phase which suppose to enter according to game win or lose statement
      s = s.gameOver? {...s,exit:s.exit.concat(s.bullets,s.aliensBullets), highScore:s.highScore} :s
      s = s.gameWin? {...s,exit:s.exit.concat(s.bullets,s.aliensBullets), highScore:s.highScore, victory:s.victory} :s
      updateView(s)
      return s.gameOver? {...INITIAL_STATE, highScore:s.highScore}: s.gameWin? {...INITIAL_STATE, stage: s.stage+1, score:s.score, highScore:s.highScore}:s
  }
  
  // Modification and adaption of reduceState in Asteroid Game
  const reduceState = (s: State, e:Move|Tick|Shoot)=>
  // move stream
  e instanceof Move ? 
  // check whether x of the current state is within boundary or not
  s.ship.x+e.lrDirection > xLimit-50 || s.ship.x+e.lrDirection < 50?s:
  {
  // change direction
  ...s,ship:{...s.ship, x: s.ship.x+e.lrDirection, xDirection: e.lrDirection}
  }: 
  // handle shoot action according to the state phase 
  e instanceof Shoot ? {
    ...s, bullets: s.aliens.length>15?s.bullets.concat([createBullet(s,0,Constant.SHIP_PHASE1_BULLET_SPEED,1)]):
      s.aliens.length>1?s.bullets.concat(createDoubleBullet(s,Constant.SHIP_PHASE2_BULLET_SPEED)):
      s.bullets.concat(createTripleBullet(s,Constant.SHIP_PHASE3_BULLET_SPEED))
    , objCount: s.objCount+1
  }:
  tick(s,e.elapsed);
  // tick

  const subscription = interval(5).pipe(
    map(elapsed=>new Tick(elapsed)),
    merge(moveLeft,moveRight, stopMove, shoot),
    scan(reduceState, INITIAL_STATE)
  ).subscribe(updateView);
}

  
// the following simply runs your pong function on window load.  Make sure to leave it in place.
if (typeof window != 'undefined')
  window.onload = ()=>{
    spaceinvaders();
  }
