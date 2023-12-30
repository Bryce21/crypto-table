

const { WebSocketServer } = require('ws')

const { Kafka } = require('kafkajs')

const kafka = new Kafka({
    clientId: 'crypto-consumer',
    brokers: ['localhost:29092'],
})
const port = 9080
console.log(`Starting websocket server on port: ${port}`)
const wss = new WebSocketServer({ port });

const consumer = kafka.consumer({ groupId: 'crypto-consumer' })

wss.on('listening', async () => {
    console.log(`Websocket server running on port: ${port}`)
    console.log('Testing connection...')
    await consumer.connect()
    console.log('Connected to consumer!')
    await consumer.subscribe({ topic: 'dataTopic', fromBeginning: false })
    console.log('Subscribed to data topic!')
    await consumer.disconnect()
})

wss.on('connection', async function connection(ws) {
    console.log('Client connected')
    ws.on('error', console.error);
    await consumer.connect()
    await consumer.subscribe({ topic: 'dataTopic', fromBeginning: false })
    await consumer.run({
        eachMessage: async ({ topic, partition, message }) => {
            console.log({
                value: message.value.toString(),
            })
            ws.send(message.value)
        },
    })
});

// wss.on('close', async () => {
//     console.log('websocket server close')
//     await consumer.disconnect()
// })
