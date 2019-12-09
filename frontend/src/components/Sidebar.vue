<template>
    <div class="sidebar">
        <h1 class="subtitle">Metrics</h1>
        <hr>
        <div v-show="Object.keys(metrics).length > 0">
            <p>Number of Clones: {{ metrics["numberOfClones"] }}</p>
            <p>Number of Clone Classes: {{ metrics["numberOfCloneClasses"] }}</p>
            <p>Percentage of Duplicated SLOC: {{ metrics["percentageOfDuplicatedLines"] }}%</p>
            <hr>
            <button @click="focusOnNode(retrieveLargestCloneClassNodeId())" class="button is-info">
                Focus on Largest Clone Class
            </button>
            <hr>
            <div v-if="selectedNode && connectedNodes">
                <h2 class="subtitle">Selected Node</h2>
                <div class="list selected-node">
                    <a @click="focusOnNode(selectedNode.id)" class="list-item">{{ selectedNode.label }}</a>
                </div>
                <p v-if="selectedNode.value">Mass: {{ selectedNode.value }}</p>
                <br>
                <p>Affected Units:</p>
                <br>
                <div class="list connected-nodes">
                    <a
                            :key="connectedNode.id"
                            @click="focusOnNode(connectedNode.id)" class="list-item"
                            v-for="connectedNode in connectedNodes"
                    >
                        {{ connectedNode.id }} ({{ connectedNode.lineCount }})
                    </a>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'Sidebar',
        data() {
            return {
                selectedNode: null,
                connectedNodes: null,
            }
        },
        mounted() {
            this.$root.$on('selected-node', (node) => {
                this.selectedNode = node.selectedNode;
                this.connectedNodes = node.connectedNodes;
            });
        },
        methods: {
            focusOnNode(nodeId) {
                this.$root.$emit('focus-on-node', nodeId);
            },
            retrieveLargestCloneClassNodeId() {
                return this.metrics["largestCloneClass"][0]["hash"];
            },
        },
        props: {
            metrics: {
                type: Object,
            },
        },
    }
</script>

<style scoped>
    .sidebar {
        padding: 2em;
    }

    h1 {
        text-align: center;
    }

    .selected-node, .connected-nodes {
        max-height: 20vh;
        overflow-y: scroll;
    }
</style>
