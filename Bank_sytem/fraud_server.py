import grpc
from concurrent import futures
import time
import fraud_engine_pb2
import fraud_engine_pb2_grpc


class FraudDetectionServiceServicer(fraud_engine_pb2_grpc.FraudDetectionServiceServicer):

    def AnalyzeTransaction(self, request, context):
        transaction_id = request.transaction_id
        amount = request.amount

        print(f"Analizando transação {transaction_id} no valor de R$ {amount}")

        risk_score = 0.05
        is_approved = True
        reason = ""

        if amount > 10000:
            risk_score = 0.95
            is_approved = False
            reason = "ANOMALOUS_AMOUNT_BLOCKED"

        time.sleep(0.05)

        return fraud_engine_pb2.RiskAnalysisResponse(
            transaction_id = transaction_id,
            risk_score = risk_score,
            is_approved = is_approved,
            rejection_reason = reason
        )

def serve():

    server = grpc.server(futures.ThreadPoolExecutor(max_workers = 10))

    fraud_engine_pb2_grpc.add_FraudDetectionServiceServicer_to_server(FraudDetectionServiceServicer(), server)

    server.add_insecure_port('[::]:50051')
    print("Motor antifraude ativo na porta 50051...")

    server.start()
    server.wait_for_termination()

if __name__ == "__main__":
    serve()