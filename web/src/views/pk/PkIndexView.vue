<template>
    <PlayGround />
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
// onMounted指当组件被挂载之后所执行的函数，onUnmounted指当组件被卸载后所执行的函数
import { onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'

export default {
    components: {
        PlayGround
    },
    setup() {
        const store = useStore();
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.id}/`;
        let socket = null;
        onMounted(() => {
            socket = new WebSocket(socketUrl);

            socket.onopen = () => {
                console.log("connected!");
            }

            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                console.log(data);
            }

            socket.onclose = () => {
                console.log("disconnected!");
            }
        });

        onUnmounted(() => {
            socket.close();
        })
    }
}
</script>

<style scoped>
</style>