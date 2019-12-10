<template>
    <div id="throbber-container">
        <RingLoader :color="color" :loading="isLoading"/>
    </div>
</template>

<script>
    import RingLoader from 'vue-spinner/src/RingLoader.vue';


    export default {
        components: {
            RingLoader,
        },
        props: {
            isLoading: Boolean,
            color: String,
        },
    }
</script>

<style scoped>
    #throbber-container {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        transform: scale(1.55);
    }
</style>
