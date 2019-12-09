<template>
    <Network
            ref="graph"
            :nodes="graphNodes"
            :edges="graphEdges"
            :options="graphOptions"
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
                graphNodeIds: new Set(),
                graphNodes: [],
                graphEdges: [],
                graphOptions: {
                    nodes: {
                        font: {
                            align: 'left',
                            size: 100,
                        },
                        margin: 100,
                    },
                    edges: {
                        scaling: {
                            customScalingFunction: this.getEdgeScalingFunction(),
                        },
                        selectionWidth: this.getSelectionWidthFunction(),
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
        methods: {
            rerenderGraph() {
                this.flushGraph();
                this.constructGraph(this.graphData["nodes"], this.graphData["edges"]);
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
                            renderedNode = this.buildUnitNode(node.id, node.identifier);
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
            buildUnitNode(id, identifier) {
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
                }
            },
            buildEdge(from, to) {
                return {
                    from: from,
                    to: to,
                    color: {
                        color: 'green',
                    },
                    width: 100,
                }
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
            getEdgeScalingFunction() {
                return function (min, max, total, value) {
                    if (max === min) {
                        return 0.5;
                    } else {
                        const scale = 1 / (max - min);
                        return Math.max(0, (value - min) * scale);
                    }
                }
            },
            getSelectionWidthFunction() {
                return function (width) {
                    return width * 5;
                }
            }
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
