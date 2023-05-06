<template>
    <div class="row">
        <div class="col-3">
            <div class="card" style="margin-top: 20px;" v-if="$store.state.pk.status === 'playing'">
                <button type="button" class="btn btn-primary">Tips</button>
                <div class="card-body">
                    <table class="table" style="text-align: center;">
                        <tbody>
                            <tr>
                            <td>我的持方：<canvas :id="parseInt($store.state.pk.b_id) === parseInt($store.state.user.id) ? 2 : 1" width="30" height="30"></canvas></td>
                            </tr>
                            <tr>
                            <td>{{ $store.state.pk.opponent_username }}的持方：<canvas :id="parseInt($store.state.pk.b_id) === parseInt($store.state.user.id) ? 1 : 2" width="30" height="30"></canvas></td>
                            </tr>
                            <tr>
                            <td>
                                <div class="container">
                                    <div class="row">
                                        <div class="col">
                                            <div class="row">
                                                <div class="col-auto mx-auto">
                                                <button type="button" class="btn btn-secondary"><b>W</b></button>
                                                </div>
                                            </div>
                                            <div class="row justify-content-center">
                                                <div class="col-auto mx-auto">
                                                    <div class="btn-group" role="group">
                                                        <button type="button" class="btn btn-secondary"><b>A</b></button>
                                                        <button type="button" class="btn btn-secondary"><b>S</b></button>
                                                        <button type="button" class="btn btn-secondary"><b>D</b></button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                控制四个方向
                            </td>
                            </tr>
                            <tr>
                            <td style="text-align: left;">玩家轮流操作自己的Bot，若连续操作只有最后一次有效</td>
                            </tr>
                            <tr>
                            <td style="text-align: left;">按下按键无反应请尝试鼠标点击地图后再操作</td>
                            </tr>
                            <tr>
                            <td style="text-align: left;">慢慢来，10秒内无操作才视为失败</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>    
            <div class="card" style="margin-top: 20px;" v-if="$store.state.record.is_record">
                <button type="button" class="btn btn-primary">Tips</button>
                <div class="card-body">
                    <table class="table" style="text-align: center;">
                        <tbody>
                            <tr>
                            <td>{{ $store.state.pk.a_username }}的持方：<canvas id="1" width="30" height="30"></canvas></td>
                            </tr>
                            <tr>
                            <td>{{ $store.state.pk.b_username }}的持方：<canvas id="2" width="30" height="30"></canvas></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>         
        </div>
        <div class="col-8 mx-auto">
            <div class="playground">
                <GameMap />
            </div>
        </div>
    </div>
</template>

<script>
import GameMap from './GameMap.vue'

export default {
    components: {
        GameMap,
    },

    mounted() {
        var canvas = document.getElementById(1);
        var ctx1 = canvas.getContext("2d");
        ctx1.beginPath();
        ctx1.arc(15, 15, 13, 0, 2 * Math.PI);
        ctx1.fillStyle = "#28983D";
        ctx1.fill();
        ctx1.beginPath();
        ctx1.arc(9, 15, 2, 0, 2 * Math.PI);
        ctx1.fillStyle = "black";
        ctx1.fill();
        ctx1.beginPath();
        ctx1.arc(21, 15, 2, 0, 2 * Math.PI);
        ctx1.fillStyle = "black";
        ctx1.fill();
        canvas = document.getElementById(2);
        var ctx2 = canvas.getContext("2d");
        ctx2.beginPath();
        ctx2.arc(15, 15, 13, 0, 2 * Math.PI);
        ctx2.fillStyle = "#BD0303";
        ctx2.fill();
        ctx2.beginPath();
        ctx2.arc(9, 15, 2, 0, 2 * Math.PI);
        ctx2.fillStyle = "black";
        ctx2.fill();
        ctx2.beginPath();
        ctx2.arc(21, 15, 2, 0, 2 * Math.PI);
        ctx2.fillStyle = "black";
        ctx2.fill();
    }
}
</script>

<style scoped>
div.playground {
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
}
</style>