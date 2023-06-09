// store用来存储用户信息，以便每次打开不同的页面都能读取用户信息
export default {
    state: {
        status: "matching",// 用户的状态有两种：matching和playing
        socket: null,
        opponent_username: "",// 记录用户当前竞争对手的用户名
        opponent_photo: "",// 记录用户当前竞争对手的头像
        gamemap: null,
        a_username: "",
        a_id: 0,
        a_sx: 0,
        a_sy: 0,
        b_username: "",
        b_id: 0,
        b_sx: 0,
        b_sy: 0,
        gameObject: null,
        loser: "none", // none、all、A、B
    },
    getters: {
    },
    mutations: { // mutations用来修改用户信息，一般放同步操作的函数
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGamemap(state, game) {
            state.gamemap = game.map;
            state.a_username = game.a_username;
            state.a_id = game.a_id;
            state.a_sx = game.a_sx;
            state.a_sy = game.a_sy;
            state.b_username = game.b_username;
            state.b_id = game.b_id;
            state.b_sx = game.b_sx;
            state.b_sy = game.b_sy;
        },
        updateGameObject(state, gameObject) {
            state.gameObject = gameObject;
        },
        updateLoser(state, loser) {
            state.loser = loser;
        }
    },
    actions: { //异步操作只能放到actions里，如：从服务器拉取信息            
    },
    modules: {
    }
}