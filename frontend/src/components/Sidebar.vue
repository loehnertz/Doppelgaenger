<template>
    <div class="sidebar">
        <div v-show="Object.keys(metrics).length > 0">
            <p>Number of Clones: {{ metrics["numberOfClones"] }}</p>
            <p>Number of Clone Classes: {{ metrics["numberOfCloneClasses"] }}</p>
            <p>Percentage of Duplicated SLOC: {{ metrics["percentageOfDuplicatedLines"] }}%</p>
            <hr>
            <button @click="focusOnNode(retrieveLargestCloneClassNodeId())" class="button is-info">
                Largest Clone Class
            </button>
            <hr>
            <basic-select
                    :options="searchOptions"
                    @select="selectedUnitNodeHandler"
                    placeholder="Search"
            />
            <hr>
            <div v-if="selectedNode && connectedEdges">
                <h2 class="subtitle is-4">Clone Class</h2>
                <div class="list selected-node">
                    <a @click="focusOnNode(selectedNode.id)" class="list-item">{{ selectedNode.label }}</a>
                </div>
                <p v-if="selectedNode.value">Mass: {{ selectedNode.value }}</p>
                <br>
                <p class="subtitle is-5">Affected Units:</p>
                <div class="list connected-nodes">
                    <a
                            :key="connectedEdge.id"
                            @click="focusOnNode(connectedEdge.to)" class="list-item"
                            v-for="connectedEdge in connectedEdges"
                    >
                        {{ connectedEdge.to }} ({{ renderRange(connectedEdge.range) }})
                    </a>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import {BasicSelect} from 'vue-search-select'


    export default {
        name: 'Sidebar',
        components: {
            BasicSelect,
        },
        computed: {
            searchOptions: function () {
                const unitNodeIds = this.unitNodes.map((unitNode) => (unitNode.id)).sort();
                return [...new Set(unitNodeIds)].map((unitNodeId) => ({value: unitNodeId, text: unitNodeId}));
            },
        },
        data() {
            return {
                selectedNode: '',
                connectedEdges: '',
                selectedUnitNode: '',
            }
        },
        mounted() {
            this.$root.$on('selected-node', (node) => {
                this.selectedNode = node.selectedNode;
                this.connectedEdges = node.connectedEdges;
            });
        },
        methods: {
            focusOnNode(nodeId) {
                this.$root.$emit('focus-on-node', nodeId);
                this.selectedUnitNode = nodeId;
            },
            retrieveLargestCloneClassNodeId() {
                return this.metrics["largestCloneClass"][0]["hash"];
            },
            renderRange(range) {
                return `${range["begin"]["line"]}:${range["begin"]["column"]} – ${range["end"]["line"]}:${range["end"]["column"]}`;
            },
            selectedUnitNodeHandler(unitNode) {
                this.selectedUnitNode = unitNode.value;
                this.focusOnNode(this.selectedUnitNode);
            },
        },
        props: {
            metrics: {
                type: Object,
            },
            unitNodes: {
                type: Array,
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
        max-height: 15vh;
        overflow-y: scroll;
    }
</style>
