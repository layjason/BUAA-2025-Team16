<template>
    <div style="width: 100%">
        <v-carousel cycle hide-delimiter-background show-arrows="hover" 
            height="510" style="width: 100%" interval="10000" direction="vertical" verti
        >
            <!-- 一页 -->
            <v-carousel-item v-for="(entry, index) in guideEntry" :key="index">
                <div style="display: flex; flex-direction: column;">
                    <Image :src="entry.src" fit="contain" width="100%"/>
                    <h3 class="title-light" style="margin-top: -42px; margin-left: 10px; z-index: 2; font-size: 30px; text-shadow: 2px 2px 3px black;">{{ entry.title }}</h3>
                    <p class="text-dark" style="margin-left: 10px; font-size: 16px; ">{{ entry.content }}</p>
                </div>
            </v-carousel-item>
        </v-carousel>
    </div>
</template>

<script setup>
import '@/assets/texts.css'
const props = defineProps({
    guideEntry: Object
});
</script>