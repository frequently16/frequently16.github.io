const AC_GAME_OBJECTS = [];

export class AcGameObject{
    constructor(){
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0;//记录这一帧到上一帧之间的时间间隔
        this.has_called_start = false;//用于判断这一帧是不是第一帧
    }

    start() {//只执行一次

    }

    update() {//每一帧执行一次，除了第一帧之外

    }
    on_destroy() {//删除之前执行

    }
    destroy() {
        for(let i in AC_GAME_OBJECTS){//在js中，in用于遍历下标，of用于遍历值
            const obj = AC_GAME_OBJECTS[i];
            if(obj === this){//如果发现obj等于当前对象的话，就把它删掉
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}
let last_timestamp; //上一次执行的时刻
// 实现每秒钟所有的游戏对象刷新60次
const step = timestamp => {
    for(let obj of AC_GAME_OBJECTS){
        if(! obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp;
    requestAnimationFrame(step)
}
// requestAnimationFrame()这个函数会在浏览器渲染下一帧之前执行一次其括号里的函数
requestAnimationFrame(step)