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
                            node: (values, id) => {
                                const selectedNode = this.findNodeById(id);
                                if (selectedNode.type === 'unit') return;
                                const connectedNodeIds = this.$refs["graph"].getConnectedNodes(id);
                                const connectedNodes = connectedNodeIds.map((nodeId) => this.findNodeById(nodeId));
                                this.$root.$emit('selected-node', {selectedNode, connectedNodes})
                            }
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
                setTimeout(() => this.fitAnimated(), 1000);
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
                            renderedNode = this.buildCloneClassNode(node.id, node.content, node.mass);
                            break;
                        case 'unit':
                            renderedNode = this.buildUnitNode(node.id, node.identifier, node.range.lineCount);
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
                    this.graphEdges.push(this.buildEdge(edge.from, edge.to));
                }
            },
            buildCloneClassNode(id, content, mass) {
                return {
                    id: id,
                    label: content,
                    title: this.convertWhitespaceCharactersToHtml(content),
                    value: mass,
                    borderWidth: 5,
                    shape: 'box',
                    color: {
                        background: 'whitesmoke',
                        border: 'blue',
                    },
                }
            },
            buildUnitNode(id, identifier, lineCount) {
                return {
                    id: id,
                    label: identifier,
                    title: this.convertWhitespaceCharactersToHtml(identifier),
                    borderWidth: 5,
                    shape: 'box',
                    color: {
                        background: 'whitesmoke',
                        border: 'firebrick',
                    },
                    lineCount,
                }
            },
            buildEdge(from, to) {
                return {
                    from: from,
                    to: to,
                    color: {
                        color: 'green',
                        highlight: 'lime',
                    },
                    width: 100,
                }
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
                    duration: 2500,
                    easingFunction: 'easeInOutQuad',
                };
                this.$refs["graph"].fit({animation: options});
            },
            findNodeById(nodeId) {
                return this.graphNodes.find((node) => {
                    return node.id === nodeId;
                });
            },
            findEdgeByFromTo(from, to) {
                return this.graphEdges.find((edge) => {
                    return edge.from === from && edge.to === to;
                });
            },
            findEdgeIndexByFromTo(from, to) {
                return this.graphEdges.findIndex((edge) => {
                    return edge.from === from && edge.to === to;
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
