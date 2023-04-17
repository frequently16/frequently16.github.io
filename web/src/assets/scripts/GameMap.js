import { AcGameObject } from "./AcGameObject";//如果这个类定义时是export，就要加{}; 如果这个类定义时是export default，就要加{}.
import { Snake } from "./Snake";
import { Wall } from "./Wall";
export class GameMap extends AcGameObject{
    constructor(ctx, parent){//ctx表示画布；parent表示画布的父元素，用来动态修改画布的长宽
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;

        this.rows = 13;
        this.cols = 14;

        this.inner_walls_count = 20;//设置障碍物数目
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#28983D", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#BD0303", r: 1, c: this.cols - 2}, this),
        ];
    }


    check_connectivity(g, sx, sy, tx, ty){//判断游戏地图是否连通
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = true;

        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        for(let i = 0; i < 4; i++){
            let x = sx + dx[i], y = sy + dy[i];
            if(!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }
        return false;
    }



    create_walls(){
        const g = [];
        for(let r = 0; r < this.rows; r++){
            g[r] = [];
            for(let c = 0; c < this.cols; c++){
                g[r][c] = false;
            }
        }

        //给四周加上障碍物
        for(let r = 0; r < this.rows; r++){
            g[r][0] = g[r][this.cols - 1] = true;
        }
        for(let c = 0; c < this.cols; c++){
            g[0][c] = g[this.rows - 1][c] = true;
        }

        //创建随机障碍物
        for(let i = 0; i < this.inner_walls_count; i++){
            for(let j = 0; j < 1000; j++){//防止出现随机生成的两个障碍物在地图中占据同一位置，才会有这层循环
                let r = 1 + parseInt(Math.random() * (this.rows - 2));
                let c = 1 + parseInt(Math.random() * (this.cols - 2));//让随机生成的障碍物永远不会在地图四周边缘
                if(g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
                break;
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g));
        //如果障碍物使得地图不连通，则不画障碍物，直接返回false
        if(! this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2)) return false;

        //如果障碍物分布符合要求，就把障碍物画在游戏地图上
        for(let r = 0; r < this.rows;  r++){
            for(let c = 0; c < this.cols; c++){
                if(g[r][c]){
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        
        //如果游戏地图左下、右上两个角连通，则返回true
        return true;
    }

    add_listening_events() {
        this.ctx.canvas.focus();

        const[snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if(e.key === 'w') snake0.set_direction(0);
            else if(e.key === 'd') snake0.set_direction(1);
            else if(e.key === 's') snake0.set_direction(2);
            else if(e.key === 'a') snake0.set_direction(3);
            else if(e.key === 'ArrowUp') snake1.set_direction(0);
            else if(e.key === 'ArrowRight') snake1.set_direction(1);
            else if(e.key === 'ArrowDown') snake1.set_direction(2);
            else if(e.key === 'ArrowLeft') snake1.set_direction(3);
        });
    }

    start(){//这个for循环的目的是保证生成障碍物后游戏地图左下、右上两个角连通
        for(let i = 0; i < 1000; i++){
            if(this.create_walls())
                break;
        }
        this.add_listening_events();
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    check_ready() {//判断两条蛇是否都准备好下一回合了
        for(const snake of this.snakes) {
            if(snake.status !== "idle") return false;
            if(snake.direction === -1) return false;
        }
        return true;
    }

    next_step(){//让两条蛇都进入下一回合
        for(const snake of this.snakes){
            snake.next_step();
        }
    }

    check_valid(cell){//检测目标位置是否合法： 没有撞到两条蛇的身体和障碍物
        for(const wall of this.walls){//如果蛇撞墙了就返回false
            if(wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for(const snake of this.snakes){
            let k = snake.cells.length;
            if(! snake.check_tail_increasing()){//蛇尾会前进的时候，蛇尾不要判断
                k--;
            }
            for(let i = 0; i < k; i++){//如果蛇会碰到蛇尾，就返回false
                if(snake.cells[i].r === cell.r && snake.cells[i].c ===cell.c)
                    return false;
            }
        }
        return true;
    }

    update() {
        this.update_size();
        if(this.check_ready()){
            this.next_step();
        }
        this.render();
    }

    render(){
        const color_even = "#23A8F2", color_odd = "#006EB1";
        for (let r = 0; r < this.rows; r++){
            for (let c = 0; c < this.cols; c++){
                if((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                }else{
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}






