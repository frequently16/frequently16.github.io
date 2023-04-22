// store用来存储用户信息，以便每次打开不同的页面都能读取用户信息
export default {
    state: {
        status: "matching",// 用户的状态有两种：matching和playing
        socket: null,
        opponent_username: "",// 记录用户当前竞争对手的用户名
        opponent_photo: "",// 记录用户当前竞争对手的头像
        gamemap: null,
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
        updateGamemap(state, gamemap) {
            state.gamemap = gamemap;
        }
    },
    actions: { //异步操作只能放到actions里，如：从服务器拉取信息            
    },
    modules: {
    }
}