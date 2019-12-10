<template>
    <Network
            :edges="graphEdges"
            :nodes="graphNodes"
            :options="graphOptions"
            ref="graph"
    >
    </Network>
</template>

<script>
    import {Network} from 'vue2vis';


    const RandomSeed = 7;

    export default {
        name: 'Graph',
        components: {
            Network,
        },
        data() {
            return {
                selectedNode: '',
                graphNodeIds: new Set(),
                graphNodes: [],
                graphEdges: [],
                graphOptions: {
                    nodes: {
                        chosen: {
                            node: this.nodeSelectionHandler,
                        },
                        font: {
                            align: 'left',
                            size: 100,
                        },
                        margin: 100,
                    },
                    layout: {
                        randomSeed: RandomSeed,
                    },
                    manipulation: false,
                    physics: {
                        forceAtlas2Based: {
                            centralGravity: 0.005,
                            gravitationalConstant: -50000,
                        },
                        solver: 'forceAtlas2Based',
                        timestep: 5.0,
                    },
                },
            }
        },
        watch: {
            graphData: function (graphData) {
                if (graphData) this.rerenderGraph();
            },
        },
        mounted() {
            this.$root.$on('focus-on-node', (nodeId) => this.focusOnNode(nodeId));
        },
        methods: {
            rerenderGraph() {
                this.flushGraph();
                this.constructGraph(this.graphData["nodes"], this.graphData["edges"]);
                setTimeout(() => this.fitAnimated(), 2500);
            },
            flushGraph() {
                this.graphNodeIds = new Set();
                this.graphNodes = [];
                this.graphEdges = [];
            },
            constructGraph(nodes, edges) {
                if (nodes && edges) {
                    this.setNodes(nodes);
                    this.setEdges(edges);
                }
            },
            setNodes(nodes) {
                for (let node of nodes) {
                    if (this.graphNodeIds.has(node.id)) continue;

                    let renderedNode;
                    switch (node["type"]) {
                        case 'class':
                            renderedNode = this.buildCloneClassNode(node.id, node.content, node.mass, node["type"]);
                            break;
                        case 'unit':
                            renderedNode = this.buildUnitNode(node.id, node.identifier, node["type"]);
                            break;
                        default:
                            console.error(`Unknown node type '${node["type"]}'`);
                            break;
                    }
                    this.graphNodes.push(renderedNode);
                    this.graphNodeIds.add(node.id);
                }
            },
            setEdges(edges) {
                for (let edge of edges) {
                    this.graphEdges.push(this.buildEdge(edge.from, edge.to, edge.range));
                }
            },
            buildCloneClassNode(id, content, mass, type) {
                return {
                    id: id,
                    label: content,
                    title: this.convertWhitespaceCharactersToHtml(content),
                    value: mass,
                    borderWidth: 6,
                    shape: 'box',
                    color: {
                        background: 'whitesmoke',
                        border: '#3298DC',
                    },
                    type,
                }
            },
            buildUnitNode(id, identifier, type) {
                return {
                    id: id,
                    label: identifier,
                    title: this.convertWhitespaceCharactersToHtml(identifier),
                    borderWidth: 5,
                    shape: 'box',
                    color: {
                        background: 'whitesmoke',
                        border: '#E01A4F',
                    },
                    type,
                }
            },
            buildEdge(from, to, range) {
                return {
                    from: from,
                    to: to,
                    color: {
                        color: '#FFBF00',
                        highlight: '#FFBF00',
                    },
                    width: 100,
                    range,
                }
            },
            nodeSelectionHandler(values, id) {
                const selectedNode = this.findNodeById(id);
                if (selectedNode.type === 'unit') return;
                const connectedEdgeIds = this.$refs["graph"].getConnectedEdges(id);
                const connectedEdges = connectedEdgeIds.map((edgeId) => this.findEdgeById(edgeId));
                this.$root.$emit('selected-node', {selectedNode, connectedEdges});
            },
            focusOnNode(nodeId) {
                const options = {
                    scale: 0.15,
                    animation: {
                        duration: 2000,
                        easingFunction: 'easeInOutQuad',
                    }
                };
                this.$refs["graph"].focus(nodeId, options);
            },
            fitAnimated() {
                const options = {
                    animation: {
                        duration: 2500,
                        easingFunction: 'easeInOutQuad',
                    },
                    nodes: Array.from(this.graphNodeIds),
                };
                this.$refs["graph"].fit(options);
                this.$emit('fitted');
            },
            findNodeById(nodeId) {
                return this.graphNodes.find((node) => {
                    return node.id === nodeId;
                });
            },
            findEdgeById(edgeId) {
                return this.graphEdges.find((edge) => {
                    return edge.id === edgeId;
                });
            },
            convertWhitespaceCharactersToHtml(title) {
                return title.replace(/\r/g, '').replace(/\t/g, '  ').replace(/ /g, '&nbsp').replace(/\n/g, '<br>');
            },
        },
        props: {
            graphData: {
                type: Object,
            },
        },
    }
</script>

<style scoped>
    @import '~vue2vis/dist/vue2vis.css';
</style>
