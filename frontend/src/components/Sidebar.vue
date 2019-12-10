<template>
    <div class="sidebar">
        <div v-show="Object.keys(metrics).length > 0">
            <p>Number of Clones: {{ metrics["numberOfClones"] }}</p>
            <p>Number of Clone Classes: {{ metrics["numberOfCloneClasses"] }}</p>
            <p>Percentage of Duplicated SLOC: {{ metrics["percentageOfDuplicatedLines"] }}%</p>
            <hr>
            <div class="list scrollable-condensed">
                <a
                        :key="cloneClassId"
                        @click="focusOnNode(cloneClassId)"
                        class="list-item"
                        v-for="(cloneClassId, index) in cloneClasses"
                >
                    {{ renderNthLargestCloneLabel(index + 1) }}
                </a>
            </div>
            <hr>
            <basic-select
                    :options="searchOptions"
                    @select="selectedUnitNodeHandler"
                    placeholder="Search"
            />
            <hr>
            <div v-if="selectedNode && connectedEdges">
                <h2 class="subtitle is-4">Clone Class</h2>
                <div class="list scrollable-condensed">
                    <a @click="focusOnNode(selectedNode.id)" class="list-item">{{ selectedNode.label }}</a>
                </div>
                <p v-if="selectedNode.value">Mass: {{ selectedNode.value }}</p>
                <br>
                <p class="subtitle is-5">Affected Units:</p>
                <div class="list scrollable-condensed">
                    <a
                            :key="connectedEdge.id"
                            @click="selectUnitFromAffectedUnitList(connectedEdge.to, connectedEdge.range)"
                            class="list-item"
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
            selectUnitFromAffectedUnitList(unitNodeId, range) {
                this.copyUnitLocationToClipboard(unitNodeId, range);
                this.focusOnNode(unitNodeId);
            },
            focusOnNode(nodeId) {
                this.$root.$emit('focus-on-node', nodeId);
                this.selectedUnitNode = nodeId;
            },
            copyUnitLocationToClipboard(unitNodeId, range) {
                this.$copyText(`${unitNodeId} ${range["begin"]["line"]}:${range["begin"]["column"]}`);
                this.$notify({
                    title: `Copied the location of the unit '${unitNodeId}' into the clipboard`,
                    position: ['bottom', 'right']
                });
            },
            renderNthLargestCloneLabel(n) {
                return `${this.determineOrdinalSuffix(n)} Largest Clone Class`;
            },
            renderRange(range) {
                return `${range["begin"]["line"]}:${range["begin"]["column"]} – ${range["end"]["line"]}:${range["end"]["column"]}`;
            },
            selectedUnitNodeHandler(unitNode) {
                this.selectedUnitNode = unitNode.value;
                this.focusOnNode(this.selectedUnitNode);
            },
            // Adapted from: https://stackoverflow.com/a/13627586
            determineOrdinalSuffix(number) {
                let j = number % 10;
                let k = number % 100;
                if (j === 1 && k !== 11) {
                    return number + "st";
                }
                if (j === 2 && k !== 12) {
                    return number + "nd";
                }
                if (j === 3 && k !== 13) {
                    return number + "rd";
                }
                return number + "th";
            },
        },
        props: {
            metrics: {
                type: Object,
            },
            cloneClasses: {
                type: Array,
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

    .scrollable-condensed {
        max-height: 15vh;
        overflow-y: scroll;
    }
</style>
