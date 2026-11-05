import grpc
import fraud_engine_pb2
import fraud_engine_pb2_grpc

def run():

    print("Concetado ao Motor Antifraude...")
    channel = grpc.insecure_channel('localhost:50051')

    stub = fraud_engine_pb2_grpc.FraudDetectionServiceStub(channel)

    request = fraud_engine_pb2.TransactionRequest(
        transaction_id = "PIX-999-ABC",
        account_origin = "CONTA-DO-IGOR",
        account_destination = "CONTA-DA-LOJA",
        amount = 15000.00,
        timestamp = 1690000000,
        location_lat_lon = "22.9268, -43.1729"
    )

    print("Transação em análise...\n")
    response = stub.AnalyzeTransaction(request)

    print(f"---RESULTADO DA ANÁLISE---")
    print(f"ID da Transação: {response.transaction_id}")
    print(f"Status: {'APROVADO ' if response.is_approved else 'BLOQUEADO'}")

    if not response.is_approved:
        print(f"Motivo da rejeição:{response.rejection_reason}")

if __name__ == '__main__':
    run()